## Comandos para gerar as chaves RSA

### Private Key
openssl genrsa -out myprivate.pem 2048

### Public Key
openssl rsa -in myprivate.pem -pubout -outform PEM -out mypublic.pem

### Private Key no formato pkcs8 - java
openssl pkcs8 -topk8 -inform PEM -in myprivate.pem -out myprivate_pkcs8.pem -nocrypt


