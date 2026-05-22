# References — Contexto de Domínio

Cada arquivo nesta pasta contém **documentação de domínio do Domo** — informações sobre produto, design e estrutura que o agente deve consultar antes de tomar decisões de UI, nomes de rotas ou estrutura de dados.

Não é instrução de como fazer (isso fica em `skills/`) — é **o que existe** no projeto.

## Quando consultar references

- Antes de criar ou nomear uma rota de navegação → consulte o mapa de navegação
- Antes de definir uma cor ou estilo → consulte a paleta e tokens de design
- Antes de criar um novo componente → consulte o catálogo de componentes existentes
- Antes de nomear uma entidade de domínio → consulte o glossário do produto

## Tipos de reference a criar

| Arquivo | Conteúdo |
|---------|----------|
| `navigation-map.md` | Todas as rotas do `AppNavGraph`, destinos e parâmetros |
| `design-tokens.md` | Paleta de cores, tipografia, espaçamentos usados no projeto |
| `component-catalog.md` | Componentes Compose reutilizáveis existentes e onde vivem |
| `minigames-overview.md` | Status de cada minijogo (implementado / planejado) e suas pastas |
| `glossary.md` | Termos do domínio: o que é "sessão", "nível", "questão", "dificuldade" no contexto do Domo |

## Formato sugerido

Sem frontmatter obrigatório — use markdown simples. Priorize tabelas e listas curtas sobre prosa longa. O agente precisa encontrar a informação rapidamente.

```markdown
# Nome da Reference

Breve descrição do que este documento cobre.

## Seção A
...

## Seção B
...
```

## References existentes

| Arquivo | O que cobre |
|---------|-------------|
| `minigame-principios.md` | Conceito de gerador procedural, princípio unificador dos 4 jogos, checklist universal, status do repositório |
| `minigame-calculadora-veloz.md` | Visão, progressão pedagógica, formatos de pergunta, estrutura de código atual, diretrizes de implementação |
| `minigame-sequencia-secreta.md` | Visão, gramática de padrões, progressão, estrutura de código sugerida |
| `minigame-mistura-de-cores.md` | Visão, direções de pergunta, discriminação perceptiva, estrutura de código sugerida |
| `minigame-ritmo-compasso.md` | Visão, gramática rítmica, progressão de mecânica, estrutura de código sugerida |
