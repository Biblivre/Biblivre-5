/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 * 
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou 
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como 
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da 
 * Licença, ou (caso queira) qualquer versão posterior.
 * 
 * Este programa é distribuído na esperança de que possa ser  útil, 
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 * 
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import biblivre.core.file.BiblivreFile;

public class FileIOUtils {

	public static File createTempDir() throws IOException {
		final File sysTempDir = FileUtils.getTempDirectory();
		final int maxAttempts = 9;
		int attemptCount = 0;

		File newTempDir;

		do {
			attemptCount++;
			if (attemptCount > maxAttempts) {
				throw new IOException("The highly improbable has occurred! Failed to create a unique temporary directory after " + maxAttempts + " attempts.");
			}
			String dirName = UUID.randomUUID().toString();
			newTempDir = new File(sysTempDir, dirName);
		} while (newTempDir.exists());

		if (newTempDir.mkdirs()) {
			return newTempDir;
		} else {
			throw new IOException("Failed to create temp dir named " + newTempDir.getAbsolutePath());
		}
	}

	public static boolean doesNotExists(String path) {
		try {
			File file = new File(path);
			return !file.exists();
		} catch (Exception e) {
			return true;
		}
	}
	
	public static void zipFolder(File src, File dest) throws IOException	 {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		fileWriter = new FileOutputStream(dest);
		zip = new ZipOutputStream(fileWriter);

		
		FileIOUtils.addFolderToZip("", src, zip);

		zip.flush();
		zip.close();
	}

	private static void addFileToZip(String path, File src, ZipOutputStream zip, boolean flag) throws IOException {
		if (flag == true) {
			zip.putNextEntry(new ZipEntry(src.getName() + "/"));
		} else {
			if (src.isDirectory()) {
				FileIOUtils.addFolderToZip(src.getName(), src, zip);
			} else {
				byte[] buf = new byte[1024];
				int len;
				FileInputStream in = new FileInputStream(src);
				
				if (path.equals("")) {
					zip.putNextEntry(new ZipEntry(src.getName()));
				} else {
					zip.putNextEntry(new ZipEntry(path + "/" + src.getName()));
				}

				while ((len = in.read(buf)) > 0) {
					zip.write(buf, 0, len);
				}
				
				in.close();
			}
		}
	}
	
	private static void addFolderToZip(String path, File src, ZipOutputStream zip) throws IOException {
		//check the empty folder
		if (src.list().length == 0) {
			FileIOUtils.addFileToZip(path, src, zip, true);
		} else {
			// list the files in the folder
			for (String fileName : src.list()) {
				FileIOUtils.addFileToZip(path, new File(src, fileName), zip, false);
			}
		}
	}
	
	public static File unzip(File zip) throws IOException {
		File tmpDir = FileIOUtils.createTempDir();
		ZipFile zipFile = new ZipFile(zip);

		Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();

		while (entries.hasMoreElements()) {
			ZipArchiveEntry entry = entries.nextElement();

			File destination = new File(tmpDir, entry.getName());
			if (destination.isDirectory()) {
				FileUtils.forceMkdir(destination);
			} else {
				InputStream is = zipFile.getInputStream(entry);
				FileOutputStream os = FileUtils.openOutputStream(destination);
			
				try {
					IOUtils.copy(is, os);
				} finally {
					os.close();
					is.close();
				}
				
				destination.setLastModified(entry.getTime());
			}
		}
		
		ZipFile.closeQuietly(zipFile);
		
		return tmpDir;
	}

	public static File ungzipBackup(File zip) throws IOException {
		File output = new File(zip.getParent(), zip.getName() + ".sql");

		GZIPInputStream is = new GZIPInputStream(new FileInputStream(zip));
		OutputStream os = new FileOutputStream(output);

		try {
			IOUtils.copy(is, os);
		} finally {
			os.close();
			is.close();
		}

		return output;
	}
	
	public static void sendHttpFile(BiblivreFile file, HttpServletRequest request, HttpServletResponse response, boolean headerOnly) throws IOException {
		if (file != null) {
			if (!file.exists()) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				file.close();
				return;
			}

			String fileName = file.getName();
			long size = file.getSize();
			long lastModified = file.getLastModified();
			String eTag = fileName + "_" + size + "_" + lastModified;
			
			// If-None-Match header should contain "*" or ETag. If so, then return 304.
			String ifNoneMatch = request.getHeader("If-None-Match");
			if (ifNoneMatch != null && FileIOUtils.matches(ifNoneMatch, eTag)) {
				response.setHeader("ETag", eTag); // Required in 304.
				response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
				file.close();
				return;
			}

			// If-Modified-Since header should be greater than LastModified. If so, then return 304.
			// This header is ignored if any If-None-Match header is specified.
			long ifModifiedSince = request.getDateHeader("If-Modified-Since");
			if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
				response.setHeader("ETag", eTag); // Required in 304.
				response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
				file.close();
				return;
			}

			// If-Match header should contain "*" or ETag. If not, then return 412.
			String ifMatch = request.getHeader("If-Match");
			if (ifMatch != null && !FileIOUtils.matches(ifMatch, eTag)) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
				file.close();
				return;
			}

			// If-Unmodified-Since header should be greater than LastModified. If not, then return 412.
			long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
			if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
				file.close();
				return;
			}

			// Prepare some variables. The full Range represents the complete file.
			Range full = new Range(0, size - 1, size);
			List<Range> ranges = new ArrayList<Range>();

			// Validate and process Range and If-Range headers.
			String range = request.getHeader("Range");
			if (range != null) {
				// Range header should match format "bytes=n-n,n-n,n-n...". If not, then return 416.
				if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
					response.setHeader("Content-Range", "bytes */" + size); // Required in 416.
					response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
					file.close();
					return;
				}

				// If-Range header should either match ETag or be greater then LastModified. If not,
				// then return full file.
				String ifRange = request.getHeader("If-Range");
				if (ifRange != null && !ifRange.equals(eTag)) {
					try {
						long ifRangeTime = request.getDateHeader("If-Range"); // Throws IAE if invalid.
						if (ifRangeTime != -1 && ifRangeTime + 1000 < lastModified) {
							ranges.add(full);
						}
					} catch (IllegalArgumentException ignore) {
						ranges.add(full);
					}
				}

				// If any valid If-Range header, then process each part of byte range.
				if (ranges.isEmpty()) {
					for (String part : range.substring(6).split(",")) {
						// Assuming a file with length of 100, the following examples returns bytes at:
						// 50-80 (50 to 80), 40- (40 to length=100), -20 (length-20=80 to length=100).
						long start = FileIOUtils.sublong(part, 0, part.indexOf("-"));
						long end = FileIOUtils.sublong(part, part.indexOf("-") + 1, part.length());

						if (start == -1) {
							start = size - end;
							end = size - 1;
						} else if (end == -1 || end > size - 1) {
							end = size - 1;
						}

						// Check if Range is syntactically valid. If not, then return 416.
						if (start > end) {
							response.setHeader("Content-Range", "bytes */" + size); // Required in 416.
							response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
							file.close();
							return;
						}

						// Add range.
						ranges.add(new Range(start, end, size));
					}
				}
			}

			String contentType = file.getContentType();
			boolean acceptsGzip = false;
			String disposition = "inline";

			if (contentType.startsWith("text")) {
				// If content type is text, then determine whether GZIP content encoding is supported by
				// the browser and expand content type with the one and right character encoding.

				String acceptEncoding = request.getHeader("Accept-Encoding");
				acceptsGzip = acceptEncoding != null && FileIOUtils.accepts(acceptEncoding, "gzip");
				contentType += ";charset=UTF-8";
				response.setCharacterEncoding("UTF-8");
			} else if (!contentType.startsWith("image")) {
				// Else, expect for images, determine content disposition. If content type is supported by
				// the browser, then set to inline, else attachment which will pop a 'save as' dialogue.

				String accept = request.getHeader("Accept");
				disposition = accept != null && FileIOUtils.accepts(accept, contentType) ? "inline" : "attachment";
			}

			// Initialize response.
			response.reset();
			response.setBufferSize(Constants.DEFAULT_BUFFER_SIZE);
			response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("ETag", eTag);
			response.setDateHeader("Last-Modified", lastModified);
			response.setDateHeader("Expires", System.currentTimeMillis() + Constants.DEFAULT_EXPIRE_TIME);

			OutputStream output = null;
			try {
				output = response.getOutputStream();

				if (ranges.isEmpty() || ranges.get(0) == full) {
					// Return full file.
					Range r = full;
					response.setContentType(contentType);
					response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);

					if (!headerOnly) {
						if (acceptsGzip) {
							// The browser accepts GZIP, so GZIP the content.
							//response.setHeader("Content-Encoding", "gzip");
							//output = new GZIPOutputStream(output, Constants.DEFAULT_BUFFER_SIZE);
						} else {
							// Content length is not directly predictable in case of GZIP.
							// So only add it if there is no means of GZIP, else browser will hang.
							response.setHeader("Content-Length", String.valueOf(r.length));
						}

						// Copy full range.
						file.copy(output, r.start, r.length);
					}

				} else if (ranges.size() == 1) {

					// Return single part of file.
					Range r = ranges.get(0);
					response.setContentType(contentType);
					response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
					response.setHeader("Content-Length", String.valueOf(r.length));
					response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

					if (!headerOnly) {
						// Copy single part range.
						file.copy(output, r.start, r.length);
					}

				} else {

					// Return multiple parts of file.
					response.setContentType("multipart/byteranges; boundary=" + Constants.MULTIPART_BOUNDARY);
					response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

					if (!headerOnly) {
						// Cast back to ServletOutputStream to get the easy println methods.
						ServletOutputStream sos = (ServletOutputStream) output;

						// Copy multi part range.
						for (Range r : ranges) {
							// Add multipart boundary and header fields for every range.
							sos.println();
							sos.println("--" + Constants.MULTIPART_BOUNDARY);
							sos.println("Content-Type: " + contentType);
							sos.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);

							// Copy single part range of multi part range.
							file.copy(output, r.start, r.length);
						}

						// End with multipart boundary.
						sos.println();
						sos.println("--" + Constants.MULTIPART_BOUNDARY + "--");
					}
				}
			} finally {
				file.close();

				if (output != null) {
					try {
						output.close();
					} catch (IOException ignore) {
					}
				}				
			}
			
			return;
		}
	}

	/**
	 * Returns true if the given match header matches the given value.
	 * @param matchHeader The match header.
	 * @param toMatch The value to be matched.
	 * @return True if the given match header matches the given value.
	 */
	private static boolean matches(String matchHeader, String toMatch) {
		String[] matchValues = matchHeader.split("\\s*,\\s*");
		Arrays.sort(matchValues);
		return Arrays.binarySearch(matchValues, toMatch) > -1 || Arrays.binarySearch(matchValues, "*") > -1;
	}

	/**
	 * Returns a substring of the given string value from the given begin index to the given end
	 * index as a long. If the substring is empty, then -1 will be returned
	 * @param value The string value to return a substring as long for.
	 * @param beginIndex The begin index of the substring to be returned as long.
	 * @param endIndex The end index of the substring to be returned as long.
	 * @return A substring of the given string value as long or -1 if substring is empty.
	 */
	private static long sublong(String value, int beginIndex, int endIndex) {
		String substring = value.substring(beginIndex, endIndex);
		return (substring.length() > 0) ? Long.parseLong(substring) : -1;
	}

	/**
	 * Returns true if the given accept header accepts the given value.
	 * @param acceptHeader The accept header.
	 * @param toAccept The value to be accepted.
	 * @return True if the given accept header accepts the given value.
	 */
	private static boolean accepts(String acceptHeader, String toAccept) {
		if (toAccept.equals("x-download")) {
			return false;
		}
		
		String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
		Arrays.sort(acceptValues);
		return Arrays.binarySearch(acceptValues, toAccept) > -1
		|| Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1
		|| Arrays.binarySearch(acceptValues, "*/*") > -1;
	}

	private static class Range {
		long start;
		long end;
		long length;
		long total;

		/**
		 * Construct a byte range.
		 * @param start Start of the byte range.
		 * @param end End of the byte range.
		 * @param total Total length of the byte source.
		 */
		public Range(long start, long end, long total) {
			this.start = start;
			this.end = end;
			this.length = end - start + 1;
			this.total = total;
		}
	}
	
	public static File getWritablePath(String path) {
		if (StringUtils.isBlank(path)) {
			return null;
		}

		File file = new File(path);

		if (!file.isDirectory()) {
			return null;
		}
		
		if (!file.canWrite()) {
			return null;
		}

		return file;
	}
	
	public static boolean isWritablePath(String path) {
		return FileIOUtils.getWritablePath(path) != null;
	}
	
	public static long countLines(File file) {
		LineNumberReader reader = null;
		int count = 1;
		try {
			reader = new LineNumberReader(new FileReader(file));
			reader.skip(Long.MAX_VALUE);
			count = reader.getLineNumber() + 1;
			reader.close();
		} catch (Exception e) {
		}
		
		return count;
	}

	public static long countFiles(File file) {
		int count = 1;
		
		if (file != null && file.isDirectory()) {
			count =  file.list().length;
		}
		
		return count;
	}

}
