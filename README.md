# VControla - Sistema de Gestão Financeira 💰

> **Status:** 🚀 Em Produção

## 📋 Sobre o Projeto

VControla é um sistema completo de gestão financeira desenvolvido para controle de contas, transações e cartões de crédito. O projeto oferece uma API REST robusta que será consumida por uma aplicação Angular.

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Data JPA**
- **Spring Security**
- **JWT (JSON Web Token)** - Autenticação e autorização
- **PostgreSQL** - Banco de dados
- **Docker** - Containerização
- **Lombok** - Redução de código boilerplate
- **Bean Validation** - Validação de dados

### Frontend
- **Angular** - Framework frontend (em desenvolvimento)

## 🔐 Segurança

- Autenticação via JWT
- Senhas criptografadas com BCrypt
- CORS configurado para integração com Angular
- Endpoints públicos: login e cadastro
- Endpoints privados protegidos por token

## 🐳 Docker

O projeto utiliza Docker Compose para facilitar o ambiente de desenvolvimento:

```yaml
- PostgreSQL na porta 5430
```

## 📦 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+
- Docker e Docker Compose
- Node.js e Angular CLI (para o frontend)

## 🚀 Como Executar

### 1. Subir o banco de dados com Docker

```bash
docker-compose up -d
```

### 2. Compilar e executar o projeto

**No PowerShell:**
```powershell
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

**No CMD ou Bash:**
```bash
./mvnw clean install
./mvnw spring-boot:run
```

### 3. Acessar a API

A API estará disponível em: `http://localhost:8080`

## 🔌 Endpoints Principais

### Públicos (sem autenticação)

- **POST** `/usuarios` - Cadastrar novo usuário
- **POST** `/usuarios/login` - Realizar login

### Privados (requer token JWT)

- Demais endpoints requerem autenticação via header `Authorization: Bearer {token}`

## 📝 Configurações

### application.properties

```properties
# Banco de dados
spring.datasource.url=jdbc:postgresql://localhost:5430/vcontrola
spring.datasource.username=postgres
spring.datasource.password=postgres

# JWT Secret
api.security.token.secret=${JWT_SECRET:minha-chave-secreta-super-segura-vcontrola-2024}
```

### Variáveis de Ambiente (Opcional)

- `JWT_SECRET` - Chave secreta para geração de tokens JWT

## 🗂️ Estrutura do Projeto

```
vcontrola/
├── src/
│   ├── main/
│   │   ├── java/com/vcontrola/vcontrola/
│   │   │   ├── controller/        # Controladores REST
│   │   │   ├── entity/            # Entidades JPA
│   │   │   ├── repository/        # Repositórios
│   │   │   ├── service/           # Lógica de negócio
│   │   │   ├── mapper/            # Conversão DTO/Entity
│   │   │   ├── infra/security/    # Configurações de segurança
│   │   │   └── enums/             # Enumeradores
│   │   └── resources/
│   │       └── application.properties
│   └── test/                      # Testes unitários
├── docker-compose.yml
├── pom.xml
└── README.md
```

## 🎯 Funcionalidades

- ✅ Cadastro de usuários
- ✅ Login com JWT
- ✅ Gestão de contas bancárias
- ✅ Controle de transações
- ✅ Gerenciamento de cartões de crédito
- ✅ Validação de dados
- ✅ Proteção contra CORS

## 🔧 Resolução de Problemas Comuns

### Erro de CORS ao conectar com Angular

Certifique-se de que a configuração CORS está permitindo `http://localhost:4200`

### Erro ao carregar JWT Secret

Verifique se a propriedade `api.security.token.secret` está definida no `application.properties`

### Erro ao executar comandos no PowerShell

Use `;` ao invés de `&&` para concatenar comandos:

```powershell
cd C:\Users\pc\Documents\vcontrola; .\mvnw.cmd clean install
```

## 👨‍💻 Desenvolvimento

### Compilar sem executar testes

```powershell
.\mvnw.cmd clean package -DskipTests
```

### Executar apenas os testes

```powershell
.\mvnw.cmd test
```

## 📄 Licença

Este projeto está em desenvolvimento.

## 🤝 Contribuindo

Projeto em desenvolvimento ativo. Sugestões e melhorias são bem-vindas!

---

**VControla** - Seu controle financeiro simplificado 💼

