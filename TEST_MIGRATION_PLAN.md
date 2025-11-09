# 🔄 Migração de Testes - Java para Kotlin + Mockito para MockK

## Status Atual

### ✅ Completado
- SpyableAdapterDelegate convertido para Kotlin (library)
- SpyableAdapterDelegate para paging (a fazer)

### ⏳ Pendente - Testes Library (5 arquivos, ~1000 linhas)

| Arquivo | Linhas | Status | Prioridade |
|---------|--------|--------|-----------|
| AdapterDelegatesManagerTest.java | 626 | ⏳ Pendente | Alta |
| AsyncListDifferDelegationAdapterTest.java | 139 | ⏳ Pendente | Média |
| AbsDelegationAdapterTest.java | 129 | ⏳ Pendente | Média |
| AbsListItemAdapterDelegateTest.java | 93 | ⏳ Pendente | Baixa |
| ListDelegationAdapterTest.java | 61 | ⏳ Pendente | Baixa |

### ⏳ Pendente - Testes Paging (2 arquivos)

| Arquivo | Status |
|---------|--------|
| SpyableAdapterDelegate.java | ⏳ Pendente |
| PagedListDelegationAdapterTest.java | ⏳ Pendente |

## Plano de Migração

### Fase 1: Conversão Java → Kotlin ✅ (Parcial)

**Ações:**
1. ✅ Converter SpyableAdapterDelegate
2. ⏳ Converter arquivos de teste usando Android Studio
3. ⏳ Ajustar imports e sintaxe Kotlin

**Benefícios:**
- Sintaxe mais concisa
- Null-safety
- Data classes
- Smart casts

### Fase 2: Mockito → MockK ⏳

**Ações:**
1. Substituir `@Mock` por `mockk<T>()`
2. Substituir `@Spy` por `spyk()`
3. Substituir `when(...).thenReturn(...)` por `every { ... } returns ...`
4. Substituir `verify(...)` por `verify { ... }`
5. Usar `slot<T>()` para capturar argumentos
6. Usar `relaxed = true` quando apropriado

**Exemplo de Conversão:**

**Antes (Mockito):**
```kotlin
@Mock
lateinit var delegate: AdapterDelegate<String>

@Before
fun setup() {
    MockitoAnnotations.openMocks(this)
}

@Test
fun test() {
    `when`(delegate.isForViewType(any(), anyInt())).thenReturn(true)
    verify(delegate).onCreateViewHolder(any())
}
```

**Depois (MockK):**
```kotlin
private val delegate: AdapterDelegate<String> = mockk()

@Before
fun setup() {
    // No need for initialization
}

@Test
fun test() {
    every { delegate.isForViewType(any(), any()) } returns true
    verify { delegate.onCreateViewHolder(any()) }
}
```

### Fase 3: Atualizar Asserções ⏳

**Ações:**
1. Substituir `Assert.assertEquals` por `assertEquals` (kotlin.test)
2. Substituir `Assert.assertTrue` por `assertTrue`
3. Considerar usar Truth para asserções mais expressivas

**Exemplo:**
```kotlin
// Antes
Assert.assertEquals(expected, actual)

// Depois (kotlin.test)
assertEquals(expected, actual)

// Ou (Truth)
assertThat(actual).isEqualTo(expected)
```

## Dependências Necessárias

### ✅ Já Adicionadas
```toml
mockk = "1.13.14"
```

### ⏳ A Adicionar (Opcional)
```toml
kotlin-test = "2.1.0"
truth = "1.4.4"
```

## Comandos Úteis

### Converter com Android Studio (Manual)
1. Abrir arquivo .java no Android Studio
2. Code → Convert Java File to Kotlin File
3. Revisar conversão automática
4. Ajustar imports e sintaxe

### Rodar Testes
```bash
# Rodar todos os testes
./gradlew test

# Rodar testes de um módulo
./gradlew :library:test

# Rodar teste específico
./gradlew :library:test --tests AdapterDelegatesManagerTest
```

## Estimativa de Tempo

| Fase | Tempo Estimado |
|------|---------------|
| Conversão Java → Kotlin | 2-3 horas |
| Mockito → MockK | 2-3 horas |
| Ajustes e correções | 1-2 horas |
| **TOTAL** | **5-8 horas** |

## Benefícios da Migração

### Kotlin
- ✅ Código mais conciso (~30% menos linhas)
- ✅ Null-safety
- ✅ Sintaxe expressiva
- ✅ Extension functions

### MockK
- ✅ Kotlin-first (melhor integração)
- ✅ DSL mais natural
- ✅ Coroutines support
- ✅ Relaxed mocks
- ✅ Melhor mensagens de erro

## Próximos Passos

1. **Converter arquivos grandes primeiro** (AdapterDelegatesManagerTest - 626 linhas)
2. **Testar após cada conversão** para garantir funcionalidade
3. **Migrar para MockK gradualmente** (pode ser feito arquivo por arquivo)
4. **Atualizar CI/CD** para rodar testes migrados
5. **Documentar padrões** de teste em Kotlin/MockK

## Notas

- ⚠️ Alguns testes podem precisar ajustes devido a diferenças Java/Kotlin
- ⚠️ MockK tem sintaxe diferente mas mais poderosa
- ⚠️ Considerar adicionar testes novos enquanto migra
- ✅ SpyableAdapterDelegate já migrado serve como template

## Status: PLANEJADO E DOCUMENTADO ✅

Migração de testes é viável e benéfica.
Primeira classe utilitária já migrada como prova de conceito.
