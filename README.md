# Coupon Management API

Sistema de gerenciamento de cupons de desconto com Spring Boot 3 e Java 21.

## Stack

- Java 21
- Spring Boot 3.2.2
- Spring Data JPA
- H2 Database
- Maven
- Lombok
- SpringDoc OpenAPI
- JUnit 5

## Arquitetura

Clean Architecture com separação em camadas de domínio, aplicação e infraestrutura.

```
src/main/java/com/coupon/
├── domain/
│   ├── entity/
│   ├── gateway/
│   ├── usecase/
│   └── valueobject/
└── infrastructure/
    ├── config/
    ├── persistence/
    └── web/
```

## Regras de Negócio

### Criação de Cupom

- Code: 6 caracteres alfanuméricos (caracteres especiais removidos automaticamente)
- DiscountValue: mínimo 0.5
- ExpirationDate: não aceita datas passadas
- Todos os campos obrigatórios

### Exclusão

- Soft delete (status muda para DELETED)
- Não permite deletar cupom já deletado

## Executar

```bash
mvn spring-boot:run
```

Aplicação disponível em `http://localhost:8080`

## Testes

```bash
mvn test
```

23 testes cobrindo as regras de negócio principais.

## Endpoints

### POST /coupon

```json
{
  "code": "ABC123",
  "description": "Cupom de desconto",
  "discountValue": 10.50,
  "expirationDate": "2026-12-31T23:59:59",
  "published": false
}
```

### GET /coupon/{id}

Retorna dados do cupom.

### DELETE /coupon/{id}

Realiza soft delete do cupom.

## Documentação

- Swagger: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:coupondb`
  - User: `sa`
  - Password: (vazio)

## Docker

```bash
cd docker
./run.sh
```

Ou manualmente:

```bash
mvn clean package -DskipTests
docker build -t coupon-api -f docker/Dockerfile .
cd docker && docker-compose up -d
```

## CI/CD

Pipeline configurada no GitHub Actions:
- Executa testes automaticamente em push/PR
- Build e publicação de imagem Docker em push para main
