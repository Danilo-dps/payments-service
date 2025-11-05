
-----

# Payment Notification Service

Este é um serviço de backend para um sistema de pagamentos simplificado, construído com **Java 21** e **Spring Boot**. Ele gerencia usuários e lojistas, processa transações (depósitos e transferências) e utiliza **Apache Kafka** para notificações assíncronas de eventos. O serviço também é configurado para enviar notificações por e-mail usando o **Google Mail (SMTP)** com um token de acesso (App Password).

## ✨ Funcionalidades Principais

* **Autenticação e Autorização:** Cadastro e login para Usuários (`USER`) e Lojistas (`STORE`).
* **Operações Financeiras:** Endpoints para depósito e transferência de valores.
* **Gerenciamento de Entidades:** Operações CRUD para Usuários e Lojistas.
* **Mensageria Assíncrona:** Utiliza **Apache Kafka** para notificar sobre transações.
* **Notificações por E-mail:** Envia e-mails de notificação usando o **Google Mail** (requer um "Token de Acesso" / "Senha de App").
* **Testes:** Cobertura de testes unitários com **JUnit 5**.

-----

## 🛠️ Stack Tecnológica

* **Backend:** Java 21, Spring Boot 3.x, Spring Security, Spring Data JPA
* **Database:** PostgreSQL (Via Docker)
* **Mensageria:** Apache Kafka (Via Docker)
* **Testes:** JUnit 5
* **Containerização:** Docker & Docker Compose
* **Notificações:** JavaMail (com Google SMTP)

-----

## 🏁 Começando

Siga estas instruções para configurar e executar o ambiente de desenvolvimento localmente.

### Pré-requisitos

* [Java 21 (JDK)](https://askubuntu.com/questions/1492571/install-openjdk-21)
* [Apache Maven](https://maven.apache.org/download.cgi)
* [Docker](https://www.docker.com/products/docker-desktop/)
* [Docker Compose](https://docs.docker.com/compose/install/)
* Uma conta Google com **"Senhas de App"** ativada. (Veja [como gerar uma](https://support.google.com/accounts/answer/185833))

### 1\. Configuração do Ambiente

Primeiro, clone este repositório:

```bash
git clone https://github.com/Danilo-dps/payments-service.git
cd pay
```

Crie um arquivo `.env` na raiz do projeto, baseado no `docker-compose.yml`. Você também precisará adicionar as variáveis para o serviço de e-mail do Spring.

**Arquivo `.env` (Exemplo):**

```yaml
# Variáveis do Docker Compose
DB_PASSWORD : sua_senha_segura_postgres
PGADMIN_PASSWORD : sua_senha_segura_pgadmin
EMAIL_USER : seu-email@gmail.com

# Variáveis do Spring Boot (para application.yml)
# Este é o "Token de Acesso" ou "Senha de App" gerado pelo Google
SPRING_MAIL_PASSWORD : seu_token_de_acesso_google
```

**Importante:** Adicione `SPRING_MAIL_PASSWORD` ao seu arquivo `application.yml` (ou `application.properties`) para que o Spring possa usá-lo. Geralmente, você faria referência à variável de ambiente:

```yaml
# Em src/main/resources/application.properties
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${EMAIL_USER}
    password: ${SPRING_MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

### 2\. Iniciando a Infraestrutura (Docker)

O arquivo `docker-compose.yml` provisiona toda a infraestrutura necessária (Postgres, pgAdmin, Kafka, Kafka-UI).
* [docker-compose.yml](https://github.com/Danilo-dps/docker-yamls/blob/main/payments-notification/docker-compose.yml)

Para iniciar todos os serviços em background:

```bash

# Comando para ser usado na pasta que está o docker-compose.yml
# /caminho/onde/está/o/arquivo
docker compose up -d
```

### 3\. Executando a Aplicação Java

Após a infraestrutura estar rodando, você pode iniciar a aplicação Spring Boot:

```bash

# Compile e execute usando o Maven
mvn spring-boot:run
```

Ou simplesmente execute a classe principal `Application` a partir da sua IDE (IntelliJ, VSCode, etc.).

### 4\. Acessando os Serviços

* **API:** `http://localhost:8080` (ou a porta definida no seu `application.yaml`)
* **Kafka UI:** `http://localhost:8081` (ou altere para outra disponível)
* **PostgresSQL:** `localhost:5433` (ou altere para outra disponível)
* **pgAdmin 4:** `http://localhost:5051` (Login com `EMAIL_USER` e `PGADMIN_PASSWORD`)

-----

## 🧪 Testes

Este projeto utiliza **JUnit 5** para testes unitários. Para rodar a suíte de testes, execute o seguinte comando Maven:

```bash

mvn test
```

-----

## 🚀 API Endpoints

Abaixo está um resumo dos endpoints da API disponíveis.

### 🔐 Autenticação (`/auth`)

* `POST /auth/login`

    * Autentica um usuário ou lojista e retorna um token JWT.
    * **Body:** `LoginRequest`

* `POST /auth/signup/user`

    * Registra um novo usuário comum.
    * **Body:** `UserDTO`

* `POST /auth/signup/store`

    * Registra um novo lojista.
    * **Body:** `StoreDTO`

### 💸 Operações (`/operations`)

* `POST /operations/deposit`

    * Realiza um depósito na conta do usuário autenticado.
    * **Autorização:** Requer `ROLE_USER`.
    * **Body:** `DepositRequestDTO`

* `POST /operations/transfer`

    * Realiza uma transferência da conta do usuário autenticado para outro usuário ou lojista.
    * **Autorização:** Requer `ROLE_USER`.
    * **Body:** `TransactionRequest`

### 👤 Usuário (`/user`)

* `GET /user/id/{userId}`

    * Busca um usuário pelo seu UUID.

* `GET /user/email/{userEmail}`

    * Busca um usuário pelo seu e-mail.

* `PUT /user/{userId}`

    * Atualiza os dados de um usuário.
    * **Body:** `UserResponse`

* `DELETE /user/{userId}`

    * Exclui um usuário.

* `GET /user/deposit/{userId}`

    * Lista todos os depósitos realizados por um usuário.

### 🏪 Lojista (`/store`)

* `GET /store/id/{storeId}`

    * Busca um lojista pelo seu UUID.

* `GET /store/email/{storeEmail}`

    * Busca um lojista pelo seu e-mail.

* `PUT /store/{storeId}`

    * Atualiza os dados de um lojista.
    * **Body:** `StoreResponse`

* `DELETE /store/{storeId}`

    * Exclui um lojista.

-----