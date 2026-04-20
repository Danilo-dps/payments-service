# Ecossistema de Pagamentos e Notificações

Este é um sistema de pagamentos simplificado orientado a eventos, construído com **Java 21** e **Spring Boot**. A arquitetura foi dividida em microsserviços e bibliotecas para garantir separação de responsabilidades, escalabilidade e reaproveitamento de código. 

O sistema gerencia usuários e empresas, processa transações (depósitos e transferências) e utiliza **Apache Kafka** (em modo KRaft) para mensageria assíncrona, culminando no envio de notificações por e-mail.

## 🏗️ Arquitetura do Projeto

Para manter a modularidade, o ecossistema está dividido em 4 repositórios principais:

1. **[Payment Service (API Produtora)](https://github.com/Danilo-dps/payments-service)**: *(Este repositório)* Responsável por expor os endpoints REST, gerenciar as contas, processar as transações e **produzir** as mensagens de notificação no Kafka.
2. **[Notification Service (Consumidor)](https://github.com/Danilo-dps/notification-service)**: Serviço dedicado a **consumir** as mensagens do Kafka e realizar o envio de e-mails via Google Mail (SMTP).
3. **[Commons Library](https://github.com/dlil-software-maker/commons)**: Uma mini-biblioteca que consolida os `records` (DTOs) e `exceptions` comuns utilizados por ambos os microsserviços, garantindo consistência e evitando duplicação de código.
4. **[Infraestrutura (Docker Yamls)](https://github.com/Danilo-dps/docker-yamls/tree/main/payments-notification)**: Repositório centralizado com o `docker-compose.yml` para provisionar o Apache Kafka e o banco de dados.

## 🚀 Funcionalidades Principais

* **Autenticação e Autorização:** Cadastro e login para Usuários Físicos (`USER` - gerado via CPF) e Empresas (`COMPANY` - gerado via CNPJ).
* **Operações Financeiras:** Endpoints para depósito e transferência de valores.
* **Mensageria Assíncrona:** Comunicação não-bloqueante entre o processamento do pagamento e o envio do recibo/alerta utilizando Apache Kafka.
* **Notificações por E-mail:** Envio de alertas configurado via Google Mail (requer Senha de App).

## 🛠️ Stack Tecnológica

* **Linguagem & Framework:** Java 21, Spring Boot 4.x, Spring Security, Spring Data JPA
* **Banco de Dados:** MySQL (Via Docker)
* **Mensageria:** Apache Kafka rodando em modo KRaft (Via Docker)
* **Testes:** JUnit 5
* **Containerização:** Docker & Docker Compose

---

## 🚦 Como Executar o Ambiente Local

Como o projeto é dividido em múltiplos repositórios, a ordem de inicialização é importante. Siga o passo a passo abaixo:

### Pré-requisitos
* Java 21 (JDK)
* Apache Maven
* Docker e Docker Compose
* Uma conta Google com **"Senhas de App"** ativada para o Notification Service (veja [como gerar](https://support.google.com/accounts/answer/185833)).

### Passo 1: Subir a Infraestrutura (Banco e Mensageria)
Clone o repositório de infraestrutura. Antes de subir os contêineres, crie um arquivo `.env` na mesma pasta do `docker-compose.yml` (ou configure no seu ambiente) com as seguintes variáveis:

**Arquivo `.env` (Infraestrutura):**
```env
MYSQL_ROOT_PASSWORD=sua_senha_forte
MYSQL_DATABASE=payments_db
MYSQL_PORT=3306
KAFKA_PORT=9092
KAFKA_CONTROLLER_PORT=9093
```

Inicie os contêineres:
```bash
git clone [https://github.com/Danilo-dps/docker-yamls.git](https://github.com/Danilo-dps/docker-yamls.git)
cd docker-yamls/payments-notification
docker compose up -d
```
*Isso iniciará o MySQL e o Kafka na rede `app-network-payment-notification`.*

### Passo 2: Instalar a Biblioteca Commons Localmente
Para que os serviços de Pagamento e Notificação encontrem as classes compartilhadas, instale a lib *commons* no seu repositório Maven local (`.m2`):

```bash
git clone [https://github.com/dlil-software-maker/commons.git](https://github.com/dlil-software-maker/commons.git)
cd commons
mvn clean install
```

### Passo 3: Configurar e Executar o Payment Service (API)
Clone este repositório (`payments-service`). Certifique-se de que as variáveis de ambiente do banco de dados e do Kafka estejam configuradas na sua IDE (ex: na aba *Run/Debug Configurations* do IntelliJ) ou no seu sistema operacional, apontando para as mesmas portas definidas no Passo 1.

Execute a aplicação:

*A API estará disponível em `http://localhost:8080` (ou na porta definida no seu `application.yml`).*

### Passo 4: Configurar e Executar o Notification Service (Consumidor)
Siga o mesmo processo para o serviço de notificação. **Importante:** Configure as variáveis de e-mail no ambiente deste serviço para que o envio via SMTP do Google funcione corretamente:

**Variáveis `.env` (para a IDE, como exemplo, pode usar na aba Run do Intellij):**
```env
MYSQL_DATABASE=nome_escolhido_por_voce
MYSQL_ROOT_PASSWORD=senha_escolhida_por_voce
MYSQL_PORT=porta_escolhida_por_voce(porta padrão 3306)
KAFKA_PORT=porta_escolhida_por_voce(porta padrão 9096)
JWT_SECRET=(Gere chaves com: openssl rand -base64 64)
SERVER_PORT=(o padrão é 8080)
```

---

## 🧪 Testes

Este projeto utiliza **JUnit 5**. Para rodar a suíte de testes unitários do Payment Service, execute o comando abaixo no terminal ou rode diretamente pela sua IDE:

---

## 📍 API Endpoints (Payment Service)

### 🔐 Autenticação (`/auth/v1`)
* `POST /auth/v1/signup`: Registra um novo usuário (CPF gera usuário PF, CNPJ gera usuário PJ).
* `POST /auth/v1/login`: Autentica um usuário e retorna o token JWT.

### 💸 Operações (`/operations/v1`) - *Requer Role USER*
* `POST /operations/v1/deposit`: Realiza um depósito na conta do usuário logado.
* `POST /operations/v1/transfer`: Transfere saldo da conta do usuário logado para outro perfil.
* `GET /operations/v1/deposit/{profileId}`: Lista os depósitos da conta do usuário logado.
* `GET /operations/v1/transaction/{profileId}`: Lista as transferências da conta do usuário logado.

### 👤 Usuário (`/profile/v1`)
* `GET /profile/v1/id/{profileId}`: Busca um usuário pelo seu UUID.
* `GET /profile/v1/profileEmail/{profileEmail}`: Busca um usuário pelo seu e-mail.
* `PUT /profile/v1/update/{profileId}`: Atualiza os dados de um usuário.
* `DELETE /profile/v1/delete/{profileId}`: Exclui um usuário do sistema.