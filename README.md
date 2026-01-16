# 💰 VControla - Sistema de Gestão Financeira

## 📋 Sobre o Projeto

Sistema de gestão financeira desenvolvido com Spring Boot e Angular, permitindo controle completo de finanças pessoais com autenticação JWT e containerização via Docker.

**Status:** 🚀 **EM PRODUÇÃO**

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Security** - Autenticação e autorização
- **JWT (JSON Web Token)** - Gerenciamento de tokens de acesso
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL** - Banco de dados relacional
- **Lombok** - Redução de código boilerplate
- **Bean Validation** - Validação de dados

### Frontend
- **Angular** - Framework frontend
- Comunicação via API REST

### DevOps
- **Docker** - Containerização da aplicação
- **Docker Compose** - Orquestração de containers

## 📂 Estrutura do Projeto

```
vcontrola/
├── src/
│   └── main/
│       ├── java/com/vcontrola/vcontrola/
│       │   ├── controller/        # Endpoints da API
│       │   ├── entity/            # Entidades JPA
│       │   ├── repository/        # Repositórios de dados
│       │   ├── service/           # Lógica de negócio
│       │   ├── mapper/            # Conversão de DTOs
│       │   ├── infra/security/    # Configurações de segurança
│       │   └── enums/             # Enumerações
│       └── resources/
│           └── application.properties
├── docker-compose.yml
└── pom.xml
```

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+
- Docker e Docker Compose
- Node.js (para o frontend Angular)

### 1. Configurar Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto (opcional):

```env
JWT_SECRET=sua-chave-secreta-super-segura-aqui
```

**Nota:** O projeto já possui um valor padrão para desenvolvimento. Para produção, é **obrigatório** definir uma chave forte.

### 2. Iniciar o Banco de Dados

```bash
docker-compose up -d
```

Isso iniciará um container PostgreSQL na porta 5430.

### 3. Executar a Aplicação

```bash
./mvnw spring-boot:run
```

Ou com Maven instalado:

```bash
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

### 4. Executar o Frontend Angular

No diretório do projeto Angular:

```bash
npm install
ng serve
```

O frontend estará disponível em: `http://localhost:4200`

## 🔐 Autenticação

A API utiliza JWT para autenticação. Para acessar endpoints protegidos:

### 1. Cadastrar Usuário
```http
POST /usuarios
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@example.com",
  "senha": "senha123456"
}
```

### 2. Fazer Login
```http
POST /usuarios/login
Content-Type: application/json

{
  "email": "joao@example.com",
  "senha": "senha123456"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 3. Acessar Endpoints Protegidos
```http
GET /endpoint-protegido
Authorization: Bearer {seu-token-jwt}
```

## 📡 Endpoints da API

### Públicos (Sem Autenticação)
- `POST /usuarios` - Cadastrar novo usuário
- `POST /usuarios/login` - Fazer login

### Protegidos (Requer JWT)
- Outros endpoints requerem autenticação via token JWT

## 🐳 Docker

### Subir todos os serviços
```bash
docker-compose up -d
```

### Parar os serviços
```bash
docker-compose down
```

### Ver logs
```bash
docker-compose logs -f
```

## ⚙️ Configurações

### application.properties
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5430/vcontrola
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
api.security.token.secret=${JWT_SECRET:chave-padrao-dev}
```

## 🔧 Resolução de Problemas

### Problema: CORS Error ao conectar Angular
**Solução:** A configuração CORS já está ativada para `http://localhost:4200`. Certifique-se de que o backend está rodando.

### Problema: @Value("${api.security.token.secret}") não funciona
**Solução:** Verifique se está usando Spring Boot 3.2.1 (não 4.0.1) e se o application.properties tem o valor definido.

### Problema: @NotBlank não valida
**Solução:** A dependência `spring-boot-starter-validation` já está incluída. Certifique-se de usar `@Valid` nos controllers.

### Problema: JWT_SECRET não encontrado
**Solução:** Defina a variável de ambiente ou use o valor padrão em desenvolvimento (já configurado).

## 📝 Funcionalidades

- ✅ Cadastro de usuários com validação
- ✅ Autenticação via JWT
- ✅ Controle de transações financeiras
- ✅ Gestão de contas bancárias
- ✅ Gestão de cartões de crédito
- ✅ API RESTful para integração com Angular
- ✅ Segurança com Spring Security
- ✅ Containerização com Docker

## 🤝 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está em desenvolvimento.

## 👨‍💻 Desenvolvedor

Projeto desenvolvido para gestão financeira pessoal.

---

**Versão:** 0.0.1-SNAPSHOT  
**Última Atualização:** 2026-01-16

