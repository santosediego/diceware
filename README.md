# Diceware — Gerador de senhas (pt-br)

Gerador de *passphrases* (frases-senha) pelo método **Diceware**, em português.
Em vez de uma senha difícil de lembrar como `P@ssw0rd!2024`, ele monta algo como
`cavalo-janela-verde-limao-telha-barco`: fácil de decorar, difícil de quebrar.

## Por que o nome é "Diceware" se a lista é "Dadoware"?

São duas camadas diferentes:

- **Diceware** é o *método* — a técnica criada por Arnold Reinhold de sortear
  palavras (originalmente com um dado de verdade) pra formar uma passphrase. É o
  nome do conceito, independente de idioma.
- **Dadoware** é a *lista de palavras em português* (mantida pela Thoughtworks),
  feita pra usar com esse método. São 7.776 palavras — o número não é aleatório:
  é 6⁵, ou seja, cada palavra corresponde a um código de 5 dígitos de 1 a 6.

O programa leva o nome do **método**, e carrega a **lista** Dadoware.

## Como funciona

Pra cada palavra, o programa "rola" um dado de 6 faces **5 vezes**, formando um
código de 5 dígitos (ex.: `4-2-6-1-3` → `42613`). Esse código é procurado na
lista e devolve a palavra correspondente. Repete o processo até ter todas as
palavras e junta tudo com hífen.

A "rolagem" não usa um dado físico: usa `SecureRandom`, o gerador
criptograficamente seguro do Java (ver *Decisões de projeto* abaixo).

## Requisitos

- **Java 11+** (roda em modo single-file, sem precisar compilar). Testado no Java 25.
- O arquivo `lista-numerada.txt` precisa estar **na mesma pasta** que o `Diceware.java`.

## Como usar

```bash
java Diceware.java        # 6 palavras (padrão)
java Diceware.java 7      # número de palavras personalizado
```

### Exemplo de saída

```
=== Diceware pt-br (Dadoware) ===
Rolando 30 dados para 6 palavras...

Palavra 1:  🎲 4-2-6-1-3  ->  42613  ->  (palavra)
Palavra 2:  🎲 1-5-2-6-4  ->  15264  ->  (palavra)
...
----------------------------------------
Senha: palavra-palavra-palavra-palavra-palavra-palavra
Entropia: ~77.5 bits (6 palavras)
----------------------------------------
```

## Entropia por número de palavras

Cada palavra adiciona ~12,9 bits de entropia (log₂ de 7.776). A força vem **só do
sorteio**, não do tamanho da palavra — então a conta é a mesma em qualquer idioma.

| Palavras | Entropia aprox. |
|:--------:|:---------------:|
|    5     |    ~64 bits     |
|    6     |    ~77 bits     |
|    7     |    ~90 bits     |
|    8     |    ~103 bits    |

6 palavras já é um padrão forte pra uso geral. Pra uma senha-mestra (gerenciador,
cofre de criptografia), vale subir pra 7 ou 8.

## Decisões de projeto

- **`SecureRandom`, nunca `Math.random()` / `new Random()`.** Os geradores comuns
  são determinísticos a partir de uma seed — previsíveis, péssimos pra segurança.
  O `SecureRandom.nextInt(6)` ainda trata o *modulo bias* internamente, então cada
  face de 1 a 6 sai com probabilidade idêntica.
- **Rolagem fiel ao método.** O programa simula os 5 dados por palavra de
  propósito, pra deixar o método visível. Só por eficiência daria pra sortear
  direto um índice de 0 a 7775 (`nextInt(7776)`) — mesma segurança, menos didático.
- **A senha é só sua.** Gere localmente e decore (ou guarde numa cópia física
  separada). Uma passphrase que passa por um serviço remoto deixa de ser 100% sua.

## Créditos e licença

- Método **Diceware** — Arnold G. Reinhold.
- Lista **Dadoware** — Thoughtworks (<https://github.com/thoughtworks/dadoware>).
  A lista de palavras tem licença própria; confira os termos no repositório
  original antes de redistribuir o `lista-numerada.txt`.

O código deste gerador é de autoria própria e pode ser usado livremente.
