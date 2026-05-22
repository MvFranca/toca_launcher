# Skills — Guias Técnicos

Cada arquivo nesta pasta descreve **como fazer algo específico** no projeto Domo. Não é código pronto — é uma instrução estruturada para o agente seguir ao implementar.

## Quando criar uma skill

Crie uma skill quando uma tarefa técnica:
- se repete entre sessões (ex: criar uma nova tela, adicionar um minijogo)
- exige seguir um padrão específico do projeto (ex: arquitetura MVVM adotada)
- envolve integração com ferramenta externa (ex: Figma MCP, Gradle)

## Formato obrigatório

Todo arquivo de skill deve seguir esta estrutura:

```markdown
---
skill: nome-da-skill
description: Uma linha explicando o que esta skill faz
---

# Quando usar
Descreva o gatilho ou contexto em que esta skill deve ser consultada.

# Passos
1. Primeiro passo concreto
2. Segundo passo
3. ...

# Exemplo
Trecho de código, estrutura de arquivo ou saída esperada.

# Restrições
O que NÃO fazer. Padrões que devem ser evitados neste projeto.
```

## Skills existentes

| Arquivo | O que cobre |
|---------|-------------|
| *(vazio — adicione conforme cria)* | |

## Exemplos de skills a criar

- `kotlin-compose-screen.md` — como criar uma nova tela seguindo a arquitetura do projeto
- `minijogo-novo.md` — estrutura de pastas e classes ao adicionar um minijogo
- `figma-leitura.md` — como usar o MCP do Figma para ler componentes e aplicar no Domo
- `difficulty-manager.md` — como estender o sistema de dificuldade da Calculadora Veloz
