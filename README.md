# Biblivre-5

Biblioteca Livre Internacional 5.0.5


## Dependências

- [PostgreSQL 9.1](https://www.postgresql.org/);
- [Apache Tomcat 7.0](http://tomcat.apache.org/);
- [Apache Maven 3.2](https://maven.apache.org/);
- Uma IDE de sua preferência. Durante o desenvolvimento do Biblivre a IDE que utilizamos foi o [Eclipse](http://www.eclipse.org/downloads/eclipse-packages/);


## Configuração

Após baixar o código-fonte, para rodar o Biblivre em sua máquina será necessário realizar algumas configurações no seu ambiente de trabalho, conforme instruções a seguir:

### PostgreSQL

- Baixe e instale o PostgreSQL;
- Dentro da pasta `/sql/` do código-fonte, execute os seguintes comandos para criar a base de dados: 

    - `"<Caminho do PostgreSQL>\bin\psql.exe" -U postgres -f createdatabase.sql`

- O comando acima criará a base de dados biblivre4 e o usuário biblivre. O Biblivre 5 é uma grande atualização do Biblivre 4, porém incremental. Para menor impacto com os usuários que já possuíam o Biblivre 4, decidimos realizar um upgrade da base de dados em vez de criar uma nova. O usuário biblivre será criado com a senha padrão 'abracadabra'. Garanta que seu ambiente PostgreSQL não esteja aberto para conexões externas.

	- `"<Caminho do PostgreSQL>\bin\psql.exe" -U postgres -f biblivre4.sql -d biblivre4`

- O comando acima criará o schema inicial da base biblivre4.

### Apache Tomcat

- Baixe e instale o Apache Tomcat;
- Caso esteja usando o Eclipse IDE, configure a [integração com Tomcat](http://www.eclipse.org/webtools/jst/components/ws/M5/tutorials/InstallTomcat.html).

### Apache Maven

- Baixe e instale o Apache Maven;
- Caso esteja usando o Eclipse IDE, configure a [integração com o Maven](http://www.eclipse.org/m2e/);
- Nem todas as dependências do Biblivre estão disponíveis em repositórios públicos, por esse motivo é necessário instalar manualmente algumas dependências do projeto. Para tanto, a partir da pasta `/lib/` do código-fonte, execute os seguintes comandos (ou, se estiver utilizando *nix execute o script maven_deps.sh na pasta `/lib`):
	- ``` 
	  mvn install:install-file -Dfile=a2j-2.0.4.jar -DgroupId=org.jzkit -DartifactId=a2j -Dversion=2.0.4 -Dpackaging=jar
      ```
	- ```
	  mvn install:install-file -Dfile=jzkit2_core-2.2.3.jar -DgroupId=org.jzkit -DartifactId=jzkit2_core -Dversion=2.2.3 -Dpackaging=jar
      ```
	- ```
	  mvn install:install-file -Dfile=jzkit2_jdbc_plugin-2.2.3.jar -DgroupId=org.jzkit -DartifactId=jzkit2_jdbc_plugin -Dversion=2.2.3 -Dpackaging=jar
      ```
	- ```
	  mvn install:install-file -Dfile=jzkit2_service-2.2.3.jar -DgroupId=org.jzkit -DartifactId=jzkit2_service -Dversion=2.2.3 -Dpackaging=jar
      ```
	- ```
	  mvn install:install-file -Dfile=jzkit2_z3950_plugin-2.2.3.jar -DgroupId=org.jzkit -DartifactId=jzkit2_z3950_plugin -Dversion=2.2.3 -Dpackaging=jar
      ```
	- ```
	  mvn install:install-file -Dfile=jzkit2_z3950_plugin-2.2.3.jar -DgroupId=org.jzkit -DartifactId=jzkit2_z3950_plugin -Dversion=2.2.3 -Dpackaging=jar
      ```
	- ```
	  mvn install:install-file -Dfile=marc4j-2.5.1.beta.jar -DgroupId=org.marc4j -DartifactId=marc4j -Dversion=2.5.1.beta -Dpackaging=jar
      ```
	- ```
	  mvn install:install-file -Dfile=z3950server-1.0.2.jar -DgroupId=br.org.biblivre -DartifactId=z3950server -Dversion=1.0.2 -Dpackaging=jar
      ```
	- ```
	  mvn install:install-file -Dfile=itext-4.2.1.jar -DgroupId=com.lowagie -DartifactId=itext -Dversion=4.2.1 -Dpackaging=jar
      ```
	- ```
	  mvn install:install-file -Dfile=normalizer-2.6.jar -DgroupId=com.ibm.icu -DartifactId=normalizer -Dversion=2.6 -Dpackaging=jar
      ```




