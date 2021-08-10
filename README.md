Teste-Java

Como compilar: Basta clonar o projeto para o seu computador, entrar no diretório raiz do projeto (onde se encontra o pom.xml) e executar no terminal "mvn clean install".
Ao compilar, os testes unitários já serão feitos automaticamente.
Ou para executar os testes unitários separados, basta rodar mvn test no diretório raiz do projeto.

Depois de compilar, basta executar "java -jar 'path do jar gerado'" no terminal para colocar a aplicação no ar. Os endpoints estarão disponíveis a partir de "http://localhost:8080/api/".

Versão do Java utilizada: 1.8 (JDK 1.8.0_241)

Observações:

- Para descriptografar os dados, basta realizar a consulta de usuário por id, passando a chave privada no header com a tag private-key. O exemplo pode ser encontrado na collection do postman.
- Para acessar a documentação swagger, pasta acessar "http://localhost:8080/swagger-ui/" quando a aplicação estiver no ar.
