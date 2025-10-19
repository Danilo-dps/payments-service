-- Flyway migration script: Complete Database Schema
-- Version: 1.0
-- Description: Creates all tables for the payment system

-- =============================================
-- TABLE: TB_ROLES
-- =============================================
CREATE TABLE TB_ROLES (
    ROLE_ID SERIAL PRIMARY KEY,
    ROLE_NAME VARCHAR(20) NOT NULL UNIQUE
);

CREATE UNIQUE INDEX IDX_TB_ROLES_ROLE_NAME ON TB_ROLES(ROLE_NAME);

-- Insert default roles
INSERT INTO TB_ROLES (ROLE_NAME) VALUES
    ('ROLE_USER'),
    ('ROLE_STORE')
ON CONFLICT (ROLE_NAME) DO NOTHING;

-- =============================================
-- TABLE: TB_USERS
-- =============================================
CREATE TABLE TB_USERS (
    USER_ID UUID NOT NULL,
    USERNAME VARCHAR(100) NOT NULL,
    DOCUMENT_NUMBER VARCHAR(14) NOT NULL,
    USER_EMAIL VARCHAR(50) NOT NULL,
    ACCESS_HASH VARCHAR(100) NOT NULL,
    ACCOUNT_BALANCE NUMERIC(19,2) NOT NULL DEFAULT 0,
    CONSTRAINT PK_TB_USERS PRIMARY KEY (USER_ID),
    CONSTRAINT UK_USER_EMAIL UNIQUE (USER_EMAIL),
    CONSTRAINT UK_USER_CPF UNIQUE (DOCUMENT_NUMBER)
);

-- =============================================
-- TABLE: USER_ROLES (Junction table)
-- =============================================
CREATE TABLE USER_ROLES (
    USER_ID UUID NOT NULL,
    ROLE_ID INTEGER NOT NULL,
    CONSTRAINT PK_USER_ROLES PRIMARY KEY (USER_ID, ROLE_ID),
    CONSTRAINT FK_USER_ROLES_USER_ID FOREIGN KEY (USER_ID)
        REFERENCES TB_USERS(USER_ID) ON DELETE CASCADE,
    CONSTRAINT FK_USER_ROLES_ROLE_ID FOREIGN KEY (ROLE_ID)
        REFERENCES TB_ROLES(ROLE_ID) ON DELETE CASCADE
);

-- =============================================
-- TABLE: TB_STORE
-- =============================================
CREATE TABLE TB_STORE (
    STORE_ID UUID NOT NULL,
    STORE_NAME VARCHAR(100) NOT NULL,
    STORE_CNPJ VARCHAR(18) NOT NULL,
    STORE_EMAIL VARCHAR(50) NOT NULL,
    ACCESS_HASH VARCHAR(80) NOT NULL,
    ACCOUNT_BALANCE NUMERIC(19,2) NOT NULL DEFAULT 0,
    CONSTRAINT PK_TB_STORE PRIMARY KEY (STORE_ID),
    CONSTRAINT UK_STORE_EMAIL UNIQUE (STORE_EMAIL),
    CONSTRAINT UK_STORE_CNPJ UNIQUE (STORE_CNPJ)
);

-- =============================================
-- TABLE: STORE_ROLES (Junction table)
-- =============================================
CREATE TABLE STORE_ROLES (
    STORE_ID UUID NOT NULL,
    ROLE_ID INTEGER NOT NULL,
    CONSTRAINT PK_STORE_ROLES PRIMARY KEY (STORE_ID, ROLE_ID),
    CONSTRAINT FK_STORE_ROLES_STORE_ID FOREIGN KEY (STORE_ID)
        REFERENCES TB_STORE(STORE_ID) ON DELETE CASCADE,
    CONSTRAINT FK_STORE_ROLES_ROLE_ID FOREIGN KEY (ROLE_ID)
        REFERENCES TB_ROLES(ROLE_ID) ON DELETE CASCADE
);

-- =============================================
-- TABLE: TB_DEPOSIT
-- =============================================
CREATE TABLE TB_DEPOSIT (
    DEPOSIT_ID UUID NOT NULL,
    DEPOSIT_TIMESTAMP TIMESTAMP NOT NULL,
    OPERATION_TYPE VARCHAR(50) NOT NULL,
    AMOUNT NUMERIC(19,2) NOT NULL,
    USER_ID UUID NOT NULL,
    CONSTRAINT PK_TB_DEPOSIT PRIMARY KEY (DEPOSIT_ID),
    CONSTRAINT FK_TB_DEPOSIT_USER FOREIGN KEY (USER_ID)
        REFERENCES TB_USERS(USER_ID)
);

-- =============================================
-- TABLE: TB_TRANSACTIONS
-- =============================================
CREATE TABLE TB_TRANSACTIONS (
    TRANSACTION_ID UUID NOT NULL,
    AMOUNT NUMERIC(19,2) NOT NULL,
    TRANSACTION_TIMESTAMP TIMESTAMP NOT NULL,
    SENDER_USER_ID UUID NOT NULL,
    RECEIVER_USER_ID UUID,
    RECEIVER_STORE_ID UUID,
    CONSTRAINT PK_TB_TRANSACTIONS PRIMARY KEY (TRANSACTION_ID),
    CONSTRAINT FK_TB_TRANSACTIONS_SENDER_USER
        FOREIGN KEY (SENDER_USER_ID) REFERENCES TB_USERS(USER_ID),
    CONSTRAINT FK_TB_TRANSACTIONS_RECEIVER_USER
        FOREIGN KEY (RECEIVER_USER_ID) REFERENCES TB_USERS(USER_ID),
    CONSTRAINT FK_TB_TRANSACTIONS_RECEIVER_STORE
        FOREIGN KEY (RECEIVER_STORE_ID) REFERENCES TB_STORE(STORE_ID),
    CONSTRAINT CHK_TB_TRANSACTIONS_VALID_RECEIVER
        CHECK (RECEIVER_USER_ID IS NOT NULL OR RECEIVER_STORE_ID IS NOT NULL),
    CONSTRAINT CHK_TB_TRANSACTIONS_NOT_SELF_TRANSACTION
        CHECK (RECEIVER_USER_ID IS NULL OR SENDER_USER_ID != RECEIVER_USER_ID)
);

-- =============================================
-- CREATE INDEXES FOR OPTIMAL PERFORMANCE
-- =============================================

-- Indexes for TB_USERS
CREATE INDEX IDX_TB_USERS_EMAIL ON TB_USERS(USER_EMAIL);
CREATE INDEX IDX_TB_USERS_DOCUMENT ON TB_USERS(DOCUMENT_NUMBER);

-- Indexes for USER_ROLES
CREATE INDEX IDX_USER_ROLES_USER_ID ON USER_ROLES(USER_ID);
CREATE INDEX IDX_USER_ROLES_ROLE_ID ON USER_ROLES(ROLE_ID);

-- Indexes for TB_STORE
CREATE INDEX IDX_TB_STORE_EMAIL ON TB_STORE(STORE_EMAIL);
CREATE INDEX IDX_TB_STORE_CNPJ ON TB_STORE(STORE_CNPJ);

-- Indexes for STORE_ROLES
CREATE INDEX IDX_STORE_ROLES_STORE_ID ON STORE_ROLES(STORE_ID);
CREATE INDEX IDX_STORE_ROLES_ROLE_ID ON STORE_ROLES(ROLE_ID);

-- Indexes for TB_DEPOSIT
CREATE INDEX IDX_TB_DEPOSIT_USER_ID ON TB_DEPOSIT(USER_ID);
CREATE INDEX IDX_TB_DEPOSIT_TIMESTAMP ON TB_DEPOSIT(DEPOSIT_TIMESTAMP);
CREATE INDEX IDX_TB_DEPOSIT_OPERATION_TYPE ON TB_DEPOSIT(OPERATION_TYPE);

-- Indexes for TB_TRANSACTIONS
CREATE INDEX IDX_TB_TRANSACTIONS_SENDER_USER_ID ON TB_TRANSACTIONS(SENDER_USER_ID);
CREATE INDEX IDX_TB_TRANSACTIONS_RECEIVER_USER_ID ON TB_TRANSACTIONS(RECEIVER_USER_ID);
CREATE INDEX IDX_TB_TRANSACTIONS_RECEIVER_STORE_ID ON TB_TRANSACTIONS(RECEIVER_STORE_ID);
CREATE INDEX IDX_TB_TRANSACTIONS_TIMESTAMP ON TB_TRANSACTIONS(TRANSACTION_TIMESTAMP);
CREATE INDEX IDX_TB_TRANSACTIONS_AMOUNT ON TB_TRANSACTIONS(AMOUNT);

-- =============================================
-- TABLE AND COLUMN COMMENTS
-- =============================================

COMMENT ON TABLE TB_ROLES IS 'Table storing user roles and permissions';
COMMENT ON COLUMN TB_ROLES.ROLE_ID IS 'Primary key, auto-incremented ID';
COMMENT ON COLUMN TB_ROLES.ROLE_NAME IS 'Role name using enum values, unique constraint';

COMMENT ON TABLE TB_USERS IS 'Table storing user information and credentials';
COMMENT ON COLUMN TB_USERS.USER_ID IS 'Primary key, UUID generated automatically';
COMMENT ON COLUMN TB_USERS.USERNAME IS 'User full name, cannot be null, max 100 characters';
COMMENT ON COLUMN TB_USERS.DOCUMENT_NUMBER IS 'User CPF number, unique, cannot be null, max 14 characters, not updatable';
COMMENT ON COLUMN TB_USERS.USER_EMAIL IS 'User email, unique, cannot be null, max 50 characters';
COMMENT ON COLUMN TB_USERS.ACCESS_HASH IS 'User password hash, cannot be null, max 100 characters';
COMMENT ON COLUMN TB_USERS.ACCOUNT_BALANCE IS 'User account balance, defaults to 0, numeric with 2 decimal places';

COMMENT ON TABLE USER_ROLES IS 'Junction table for many-to-many relationship between users and roles';
COMMENT ON COLUMN USER_ROLES.USER_ID IS 'Foreign key referencing TB_USERS';
COMMENT ON COLUMN USER_ROLES.ROLE_ID IS 'Foreign key referencing TB_ROLES';

COMMENT ON TABLE TB_STORE IS 'Table storing store information and credentials';
COMMENT ON COLUMN TB_STORE.STORE_ID IS 'Primary key, UUID generated automatically';
COMMENT ON COLUMN TB_STORE.STORE_NAME IS 'Store name, cannot be null, max 100 characters';
COMMENT ON COLUMN TB_STORE.STORE_CNPJ IS 'Store CNPJ number, unique, cannot be null, max 18 characters, not updatable';
COMMENT ON COLUMN TB_STORE.STORE_EMAIL IS 'Store email, unique, cannot be null, max 50 characters';
COMMENT ON COLUMN TB_STORE.ACCESS_HASH IS 'Store password hash, cannot be null, max 80 characters';
COMMENT ON COLUMN TB_STORE.ACCOUNT_BALANCE IS 'Store account balance, defaults to 0, numeric with 2 decimal places';

COMMENT ON TABLE STORE_ROLES IS 'Junction table for many-to-many relationship between stores and roles';
COMMENT ON COLUMN STORE_ROLES.STORE_ID IS 'Foreign key referencing TB_STORE';
COMMENT ON COLUMN STORE_ROLES.ROLE_ID IS 'Foreign key referencing TB_ROLES';

COMMENT ON TABLE TB_DEPOSIT IS 'Table storing deposit transactions information';
COMMENT ON COLUMN TB_DEPOSIT.DEPOSIT_ID IS 'Primary key, UUID generated automatically';
COMMENT ON COLUMN TB_DEPOSIT.DEPOSIT_TIMESTAMP IS 'Timestamp when the deposit was made, cannot be null';
COMMENT ON COLUMN TB_DEPOSIT.OPERATION_TYPE IS 'Type of operation, uses enum values, cannot be null';
COMMENT ON COLUMN TB_DEPOSIT.AMOUNT IS 'Deposit amount, numeric with 2 decimal places, cannot be null';
COMMENT ON COLUMN TB_DEPOSIT.USER_ID IS 'Foreign key referencing the user who made the deposit, cannot be null';

COMMENT ON TABLE TB_TRANSACTIONS IS 'Table storing financial transactions between users and stores';
COMMENT ON COLUMN TB_TRANSACTIONS.TRANSACTION_ID IS 'Primary key, UUID generated automatically';
COMMENT ON COLUMN TB_TRANSACTIONS.AMOUNT IS 'Transaction amount, cannot be null, numeric with 2 decimal places';
COMMENT ON COLUMN TB_TRANSACTIONS.TRANSACTION_TIMESTAMP IS 'Timestamp when transaction occurred, cannot be null';
COMMENT ON COLUMN TB_TRANSACTIONS.SENDER_USER_ID IS 'Foreign key referencing the user who sent the transaction, cannot be null';
COMMENT ON COLUMN TB_TRANSACTIONS.RECEIVER_USER_ID IS 'Foreign key referencing the user who received the transaction (optional)';
COMMENT ON COLUMN TB_TRANSACTIONS.RECEIVER_STORE_ID IS 'Foreign key referencing the store that received the transaction (optional)';
COMMENT ON CONSTRAINT CHK_TB_TRANSACTIONS_VALID_RECEIVER ON TB_TRANSACTIONS IS 'Ensures at least one receiver (user or store) is specified';
COMMENT ON CONSTRAINT CHK_TB_TRANSACTIONS_NOT_SELF_TRANSACTION ON TB_TRANSACTIONS IS 'Prevents users from sending transactions to themselves';

-- =============================================
-- END OF SCHEMA CREATION
-- =============================================