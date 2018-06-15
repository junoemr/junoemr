# Counteract a bug in openapi-generator https://github.com/OpenAPITools/openapi-generator/issues/189
sed -i -e 's/models.models./models./' src/main/frontend/generated/api/*Api.ts
