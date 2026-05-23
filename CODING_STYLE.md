# Essentialist Coding Style

## 1. Explosao Sincrona de Escopo (Scope Bursting)

Expressoes encadeadas, concatenacoes ou transformacoes complexas **nunca** sao passadas diretamente como argumento de
outro metodo. Elas devem ser "explodidas" em variaveis locais `var` de escopo curto antes da chamada final.

### Regra de Ouro

> Se uma expressao contem mais de **1 metodo de transformacao** (`.replace`, `.format`, `.mini`, `.value`, `.messages`)
> ou tem mais de **60 caracteres**, ela deve ser extraida para uma variavel local.

### Exemplo — Correto

```java
var snap = this.config.value();
var messages = snap.messages();
var acceptedMsg = messages.acceptedSelf().replace("{player}", request.requester().name());
actor.sendSuccess(acceptedMsg);
```

### Exemplo — Incorreto

```java
// NUNO faca isso. O formatador vai quebrar quando os nomes crescerem.
actor.sendSuccess(this.config.value().messages().acceptedSelf().replace("{player}", request.requester().name()));
```

## 2. Clausulas de Guarda (Fail-Fast)

Use clausulas de guarda para ejetar estados invalidos imediatamente. Evite aninhamentos em piramide.

### Correto

```java
if (player == null || !player.isOnline()) {
    return;
}
// codigo principal roda livre
```

### Incorreto

```java
if (player != null) {
    if (player.isOnline()) {
        // codigo espremido
    }
}
```

## 3. Eliminacao de Operacoes Inline em Parametros

Nao passe logica complexa, concatenacoes de Strings com `.replace()` ou chamadas de metodos como argumentos de outros
metodos. Isso e o que mais causa quebras feias de linha pelo formatador.

### Correto

```java
var prompt = new DelayedTeleportPrompt(actor, teleportingMsg, teleportedMsg, cancelledMsg, failedMsg);
this.delayed.schedule(sender, destination, delay, prompt);
```

### Incorreto

```java
this.delayed.schedule(sender, destination, delay,
    new DelayedTeleportPrompt(actor, messages.teleporting().replace("{name}", warpName), ...));
```

## 4. Segmentacao por Zonas Logicas

Todo metodo refatorado segue um padrao visual previsivel, dividindo o codigo em blocos separados por uma linha em
branco:

1. **Zona de Extracao** — coleta dados e prepara variaveis locais.
2. **Zona de Validacao** — clausulas de guarda, bloqueios de nulos, checagens de opcionais.
3. **Zona de Processamento** — logica de negocio ou chamada assincrona.
4. **Zona de Efeito Colateral** — retorno do metodo ou envio de mensagem final.

## 5. Uso de `var`

Use `var` para todas as variaveis locais de escopo curto criadas por Scope Bursting. Isso reduz o ruido visual e mantem
o foco no fluxo de dados.

## 6. `ComponentUtils.mini(...)`

Nunca passe `ComponentUtils.mini(...)` diretamente como argumento de `sendMessage`, `sendActionBar`, `kick` ou
`createInventory`. Extraia para uma variavel local `var` primeiro.

### Correto

```java
var screenComponent = ComponentUtils.mini(screenMessage);
target.kick(screenComponent);
```

### Incorreto

```java
target.kick(ComponentUtils.mini(screenMessage));
```

## 7. Configs e Replaces Encadeados

Em metodos de config que fazem multiplos `.replace()`, extraia os valores intermediarios para variaveis locais antes de
aplicar.

### Correto

```java
var compactX = Numbers.compact(x);
var compactY = Numbers.compact(y);
var compactZ = Numbers.compact(z);
return toPos.replace("{x}", compactX).replace("{y}", compactY).replace("{z}", compactZ);
```

## Resumo

O codigo deve fluir **estritamente na vertical**, mantendo a margem esquerda alinhada e a margem direita completamente
longe do limite de quebra do formatador. Scope Bursting nao e opcional — e a principal tecnica de defesa contra
regressoes de formatacao.
