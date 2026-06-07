package com.devmock.backend.config;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.devmock.backend.entity.Achievement;
import com.devmock.backend.entity.Category;
import com.devmock.backend.entity.DifficultyLevel;
import com.devmock.backend.entity.EvaluationCriterion;
import com.devmock.backend.entity.InterviewType;
import com.devmock.backend.entity.Question;
import com.devmock.backend.entity.User;
import com.devmock.backend.entity.en_enum.AnswerFormat;
import com.devmock.backend.entity.en_enum.QuestionType;
import com.devmock.backend.entity.en_enum.UserRole;
import com.devmock.backend.repository.AchievementRepository;
import com.devmock.backend.repository.CategoryRepository;
import com.devmock.backend.repository.DifficultyLevelRepository;
import com.devmock.backend.repository.EvaluationCriterionRepository;
import com.devmock.backend.repository.InterviewTypeRepository;
import com.devmock.backend.repository.QuestionRepository;
import com.devmock.backend.repository.UserRepository;

@Component
@Transactional
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepo;
    private final DifficultyLevelRepository difficultyLevelRepo;
    private final CategoryRepository categoryRepo;
    private final InterviewTypeRepository interviewTypeRepo;
    private final EvaluationCriterionRepository evaluationCriterionRepo;
    private final AchievementRepository achievementRepo;
    private final QuestionRepository questionRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepo,
            DifficultyLevelRepository difficultyLevelRepo,
            CategoryRepository categoryRepo,
            InterviewTypeRepository interviewTypeRepo,
            EvaluationCriterionRepository evaluationCriterionRepo,
            AchievementRepository achievementRepo,
            QuestionRepository questionRepo,
            PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.difficultyLevelRepo = difficultyLevelRepo;
        this.categoryRepo = categoryRepo;
        this.interviewTypeRepo = interviewTypeRepo;
        this.evaluationCriterionRepo = evaluationCriterionRepo;
        this.achievementRepo = achievementRepo;
        this.questionRepo = questionRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (difficultyLevelRepo.count() > 0) {
            log.info("Data already seeded — skipping");
            return;
        }

        log.info("Seeding initial data...");

        var facil = createDifficultyLevel("Fácil", "facil", 1, BigDecimal.valueOf(1.00), "Preguntas básicas");
        var intermedio = createDifficultyLevel("Intermedio", "intermedio", 2, BigDecimal.valueOf(1.50), "Preguntas intermedias");
        var avanzado = createDifficultyLevel("Avanzado", "avanzado", 3, BigDecimal.valueOf(2.00), "Preguntas avanzadas");

        var algoritmos = createCategory("Algoritmos", "algoritmos", "Preguntas sobre algoritmos y estructuras de datos", "code-icon", 1);
        var estructurasDatos = createCategory("Estructuras de Datos", "estructuras-de-datos", "Preguntas de implementación de estructuras de datos", "database-icon", 2);
        var desarrolloWeb = createCategory("Desarrollo Web", "desarrollo-web", "Preguntas sobre desarrollo web y APIs", "globe-icon", 3);
        var basesDatos = createCategory("Bases de Datos", "bases-de-datos", "Preguntas sobre bases de datos y SQL", "server-icon", 4);
        var disenoSistemas = createCategory("Diseño de Sistemas", "diseno-de-sistemas", "Preguntas sobre diseño de sistemas y arquitectura", "layers-icon", 5);

        createInterviewType("Entrevista Técnica", "entrevista-tecnica", QuestionType.MIXED, 5, 3600,
                "Entrevista técnica estándar con tipos de preguntas mixtas");
        createInterviewType("Desafío de Código", "desafio-de-codigo", QuestionType.PRACTICAL, 3, 1800,
                "Desafío de código enfocado en ejercicios prácticos");

        createEvaluationCriterion("Corrección", "correccion", "Qué tan correcta es la solución", BigDecimal.valueOf(40.00));
        createEvaluationCriterion("Eficiencia", "eficiencia", "Qué tan eficiente es la solución", BigDecimal.valueOf(25.00));
        createEvaluationCriterion("Claridad", "claridad", "Qué tan claro y legible es el código", BigDecimal.valueOf(20.00));
        createEvaluationCriterion("Lógica", "logica", "Qué tan lógico es el enfoque", BigDecimal.valueOf(15.00));

        var admin = createAdminUser(facil);

        createAchievement("Primera Sesión", "primera-sesion", "Completa tu primera sesión de entrevista",
                "completar 1 sesión", 50);
        createAchievement("Puntuación Perfecta", "puntuacion-perfecta", "Obtén una puntuación perfecta en cualquier pregunta",
                "puntuar 100 en cualquier pregunta", 100);
        createAchievement("Rápido como un Rayo", "rapido-como-un-rayo", "Completa una pregunta en la mitad del tiempo asignado",
                "terminar una pregunta en menos del 50% del tiempo estimado", 75);
        createAchievement("Maratón", "maraton", "Completa 10 sesiones de entrevista",
                "completar 10 sesiones", 200);

        // ========================================================================
        // PREGUNTAS — 75 total (5 por cada combinación categoría × dificultad)
        // ========================================================================

        // ─── ALGORITMOS ────────────────────────────────────────────────────────────

        // --- Algoritmos / Fácil (5) ---
        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una función que verifique si una cadena dada es un palíndromo. La función debe ignorar mayúsculas, espacios y signos de puntuación.",
                "def is_palindrome(s):\n    cleaned = ''.join(c.lower() for c in s if c.isalnum())\n    return cleaned == cleaned[::-1]",
                "Limpia la cadena conservando solo caracteres alfanuméricos, convierte a minúsculas, luego compara con su reverso.",
                600, 50, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("palindromo", "cadenas", "facil"),
                algoritmos, facil, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una función que genere los primeros n números de la secuencia de Fibonacci.",
                "def fibonacci(n):\n    if n <= 1:\n        return n\n    a, b = 0, 1\n    for _ in range(2, n + 1):\n        a, b = b, a + b\n    return b",
                "Usa dos variables para mantener los dos últimos valores y actualizarlos iterativamente. Casos base: fib(0)=0, fib(1)=1.",
                600, 50, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("fibonacci", "iteracion", "facil"),
                algoritmos, facil, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una función que calcule el factorial de un número entero no negativo.",
                "def factorial(n):\n    if n == 0:\n        return 1\n    result = 1\n    for i in range(2, n + 1):\n        result *= i\n    return result",
                "El factorial de 0 es 1. Para n > 0, multiplica todos los enteros desde 1 hasta n. Puede implementarse iterativa o recursivamente.",
                480, 40, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("factorial", "iteracion", "facil"),
                algoritmos, facil, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una función que encuentre el valor máximo en un arreglo de enteros.",
                "def find_max(arr):\n    if not arr:\n        return None\n    max_val = arr[0]\n    for num in arr[1:]:\n        if num > max_val:\n            max_val = num\n    return max_val",
                "Inicializa max con el primer elemento, luego recorre el arreglo actualizando max cuando encuentres un valor mayor. Si el arreglo está vacío retorna None.",
                480, 40, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("maximo", "arreglos", "facil"),
                algoritmos, facil, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una función que cuente el número de vocales (a, e, i, o, u) en una cadena dada. Debe ignorar mayúsculas.",
                "def count_vowels(s):\n    vowels = set('aeiou')\n    count = 0\n    for ch in s.lower():\n        if ch in vowels:\n            count += 1\n    return count",
                "Convierte la cadena a minúsculas, itera sobre cada carácter y verifica si pertenece al conjunto de vocales. Usar un conjunto hace la búsqueda O(1) por carácter.",
                480, 40, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("vocales", "cadenas", "facil"),
                algoritmos, facil, admin);

        // --- Algoritmos / Intermedio (5) ---
        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una función que encuentre dos números en un arreglo que sumen un valor objetivo dado. Asume que existe exactamente una solución.",
                "def two_sum(nums, target):\n    seen = {}\n    for i, num in enumerate(nums):\n        complement = target - num\n        if complement in seen:\n            return [seen[complement], i]\n        seen[num] = i\n    return None",
                "Usa un diccionario para almacenar los números ya visitados y sus índices. Para cada número, calcula el complemento necesario para alcanzar el objetivo y verifica si ya lo has visto.",
                600, 80, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("two-sum", "hash-map", "intermedio"),
                algoritmos, intermedio, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implementa una función de búsqueda binaria en un arreglo ordenado de enteros.",
                "def binary_search(arr, target):\n    left, right = 0, len(arr) - 1\n    while left <= right:\n        mid = left + (right - left) // 2\n        if arr[mid] == target:\n            return mid\n        elif arr[mid] < target:\n            left = mid + 1\n        else:\n            right = mid - 1\n    return -1",
                "Divide el espacio de búsqueda a la mitad en cada iteración comparando el valor medio con el objetivo. Requiere que el arreglo esté ordenado. Complejidad O(log n).",
                600, 80, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("busqueda-binaria", "algoritmos", "intermedio"),
                algoritmos, intermedio, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una función que determine si una cadena que contiene solo los caracteres '(', ')', '{', '}', '[' y ']' tiene paréntesis válidos.",
                "def is_valid_parentheses(s):\n    stack = []\n    pairs = {')': '(', '}': '{', ']': '['}\n    for ch in s:\n        if ch in pairs:\n            if not stack or stack.pop() != pairs[ch]:\n                return False\n        else:\n            stack.append(ch)\n    return len(stack) == 0",
                "Usa una pila para rastrear los paréntesis de apertura. Al encontrar un cierre, verifica que coincida con el tope de la pila.",
                600, 80, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("parentesis", "pila", "intermedio"),
                algoritmos, intermedio, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una función que combine dos arreglos ordenados en un solo arreglo ordenado.",
                "def merge_sorted(arr1, arr2):\n    i, j = 0, 0\n    result = []\n    while i < len(arr1) and j < len(arr2):\n        if arr1[i] <= arr2[j]:\n            result.append(arr1[i])\n            i += 1\n        else:\n            result.append(arr2[j])\n            j += 1\n    result.extend(arr1[i:])\n    result.extend(arr2[j:])\n    return result",
                "Usa dos punteros para recorrer ambos arreglos simultáneamente, agregando el menor elemento en cada paso. Al terminar uno, agrega los elementos restantes del otro.",
                600, 80, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("merge", "ordenamiento", "intermedio"),
                algoritmos, intermedio, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una función que elimine los elementos duplicados de un arreglo ordenado, devolviendo la nueva longitud.",
                "def remove_duplicates(nums):\n    if not nums:\n        return 0\n    i = 0\n    for j in range(1, len(nums)):\n        if nums[j] != nums[i]:\n            i += 1\n            nums[i] = nums[j]\n    return i + 1",
                "Usa dos punteros: i marca la posición del último elemento único, j recorre el arreglo. Cuando nums[j] != nums[i], avanza i y copia el valor.",
                600, 80, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("duplicados", "punteros", "intermedio"),
                algoritmos, intermedio, admin);

        // --- Algoritmos / Avanzado (5) ---
        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implementa el algoritmo QuickSort para ordenar un arreglo de enteros.",
                "def quicksort(arr):\n    if len(arr) <= 1:\n        return arr\n    pivot = arr[len(arr) // 2]\n    left = [x for x in arr if x < pivot]\n    middle = [x for x in arr if x == pivot]\n    right = [x for x in arr if x > pivot]\n    return quicksort(left) + middle + quicksort(right)",
                "Selecciona un pivote, particiona el arreglo en elementos menores, iguales y mayores que el pivote, luego ordena recursivamente las particiones izquierda y derecha. Complejidad promedio O(n log n).",
                900, 120, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("quicksort", "ordenamiento", "avanzado"),
                algoritmos, avanzado, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implementa el algoritmo MergeSort para ordenar un arreglo de enteros.",
                "def merge_sort(arr):\n    if len(arr) <= 1:\n        return arr\n    mid = len(arr) // 2\n    left = merge_sort(arr[:mid])\n    right = merge_sort(arr[mid:])\n    return merge(left, right)\n\ndef merge(left, right):\n    result = []\n    i = j = 0\n    while i < len(left) and j < len(right):\n        if left[i] <= right[j]:\n            result.append(left[i]); i += 1\n        else:\n            result.append(right[j]); j += 1\n    result.extend(left[i:]); result.extend(right[j:])\n    return result",
                "Divide el arreglo en mitades recursivamente hasta tener elementos individuales, luego combina las mitades ordenadamente. Complejidad O(n log n) garantizado.",
                900, 120, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("mergesort", "ordenamiento", "avanzado"),
                algoritmos, avanzado, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una función que encuentre la subsecuencia común más larga (LCS) entre dos cadenas.",
                "def lcs(s1, s2):\n    m, n = len(s1), len(s2)\n    dp = [[0] * (n + 1) for _ in range(m + 1)]\n    for i in range(1, m + 1):\n        for j in range(1, n + 1):\n            if s1[i - 1] == s2[j - 1]:\n                dp[i][j] = dp[i - 1][j - 1] + 1\n            else:\n                dp[i][j] = max(dp[i - 1][j], dp[i][j - 1])\n    return dp[m][n]",
                "Usa programación dinámica con una tabla dp[i][j] que almacena la longitud del LCS de los prefijos s1[:i] y s2[:j]. Si los caracteres coinciden, suma 1 al valor diagonal; si no, toma el máximo entre el valor superior e izquierdo.",
                900, 120, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("lcs", "programacion-dinamica", "avanzado"),
                algoritmos, avanzado, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una función que detecte si una lista enlazada tiene un ciclo usando el algoritmo de Floyd (tortuga y liebre).",
                "def has_cycle(head):\n    slow = fast = head\n    while fast and fast.next:\n        slow = slow.next\n        fast = fast.next.next\n        if slow == fast:\n            return True\n    return False",
                "Usa dos punteros que avanzan a diferentes velocidades. Si hay un ciclo, el puntero rápido eventualmente alcanzará al lento. Complejidad O(n) con memoria O(1).",
                900, 120, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("ciclo", "lista-enlazada", "avanzado"),
                algoritmos, avanzado, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implementa el algoritmo de Dijkstra para encontrar la distancia más corta desde un nodo origen en un grafo ponderado.",
                "import heapq\n\ndef dijkstra(graph, start):\n    distances = {node: float('inf') for node in graph}\n    distances[start] = 0\n    pq = [(0, start)]\n    while pq:\n        dist, node = heapq.heappop(pq)\n        if dist > distances[node]:\n            continue\n        for neighbor, weight in graph[node]:\n            new_dist = dist + weight\n            if new_dist < distances[neighbor]:\n                distances[neighbor] = new_dist\n                heapq.heappush(pq, (new_dist, neighbor))\n    return distances",
                "Usa una cola de prioridad para explorar siempre el nodo con menor distancia acumulada. Relaja las aristas actualizando las distancias cuando encuentra un camino más corto.",
                1200, 150, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("dijkstra", "grafos", "avanzado"),
                algoritmos, avanzado, admin);

        // ─── ESTRUCTURAS DE DATOS ──────────────────────────────────────────────────

        // --- Estructuras de Datos / Fácil (5) ---
        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implementa una pila (stack) usando un arreglo. Debe incluir push, pop y peek.",
                "class Stack:\n    def __init__(self, capacity=10):\n        self.capacity = capacity\n        self.arr = [None] * capacity\n        self.top = -1\n\n    def push(self, val):\n        if self.top == self.capacity - 1:\n            raise OverflowError('Stack overflow')\n        self.top += 1\n        self.arr[self.top] = val\n\n    def pop(self):\n        if self.top == -1:\n            raise IndexError('Empty stack')\n        val = self.arr[self.top]\n        self.top -= 1\n        return val\n\n    def peek(self):\n        if self.top == -1:\n            return None\n        return self.arr[self.top]",
                "Usa un arreglo con un índice 'top' que apunta al último elemento insertado. Push incrementa top y asigna el valor. Pop devuelve el valor en top y lo decrementa.",
                600, 50, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("pila", "stack", "facil"),
                estructurasDatos, facil, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implementa una cola (queue) usando una lista enlazada. Debe incluir enqueue, dequeue y front.",
                "class Node:\n    def __init__(self, val):\n        self.val = val\n        self.next = None\n\nclass Queue:\n    def __init__(self):\n        self.front = self.rear = None\n\n    def enqueue(self, val):\n        node = Node(val)\n        if not self.rear:\n            self.front = self.rear = node\n            return\n        self.rear.next = node\n        self.rear = node\n\n    def dequeue(self):\n        if not self.front:\n            return None\n        val = self.front.val\n        self.front = self.front.next\n        if not self.front:\n            self.rear = None\n        return val",
                "Usa dos punteros (front y rear). Enqueue agrega al final, dequeue remueve del frente. Una lista enlazada permite operaciones O(1) en ambos extremos.",
                600, 50, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("cola", "queue", "facil"),
                estructurasDatos, facil, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una función que recorra una lista enlazada simple e imprima sus valores.",
                "def traverse(head):\n    current = head\n    while current:\n        print(current.val)\n        current = current.next",
                "Usa un puntero temporal que avanza nodo por nodo hasta llegar a None. Cada nodo contiene un valor y una referencia al siguiente nodo.",
                480, 40, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("lista-enlazada", "recorrido", "facil"),
                estructurasDatos, facil, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una función que encuentre el valor mínimo en un arreglo de enteros.",
                "def find_min(arr):\n    if not arr:\n        return None\n    min_val = arr[0]\n    for num in arr[1:]:\n        if num < min_val:\n            min_val = num\n    return min_val",
                "Inicializa min con el primer elemento, luego recorre el arreglo actualizando min cuando encuentres un valor menor. Complejidad O(n).",
                480, 40, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("minimo", "arreglos", "facil"),
                estructurasDatos, facil, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica qué es una tabla hash y cómo funciona internamente la resolución de colisiones.",
                null,
                "Una tabla hash almacena pares clave-valor usando una función hash que mapea cada clave a un índice del arreglo subyacente. Las colisiones ocurren cuando dos claves tienen el mismo hash. Se resuelven con encadenamiento (lista enlazada en cada bucket) o direccionamiento abierto (buscar siguiente posición disponible).",
                600, 50, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("hash-table", "colisiones", "facil"),
                estructurasDatos, facil, admin);

        // --- Estructuras de Datos / Intermedio (5) ---
        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implementa un árbol binario de búsqueda (BST) con operaciones de inserción y búsqueda.",
                "class Node { int value; Node left, right; Node(int v) { value = v; } } class BST { Node insert(Node root, int v) { if (root == null) return new Node(v); if (v < root.value) root.left = insert(root.left, v); else root.right = insert(root.right, v); return root; } boolean search(Node root, int v) { if (root == null) return false; if (root.value == v) return true; return v < root.value ? search(root.left, v) : search(root.right, v); } }",
                "Cada nodo tiene un valor, un hijo izquierdo y un hijo derecho. Insertar busca recursivamente la posición correcta. Buscar recorre recursivamente izquierda o derecha según la comparación de valores.",
                900, 100, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("bst", "arboles", "estructuras-de-datos"),
                estructurasDatos, intermedio, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implementa una pila que además de las operaciones normales tenga un método min() que devuelva el elemento mínimo en tiempo O(1).",
                "class MinStack:\n    def __init__(self):\n        self.stack = []\n        self.min_stack = []\n\n    def push(self, val):\n        self.stack.append(val)\n        if not self.min_stack or val <= self.min_stack[-1]:\n            self.min_stack.append(val)\n\n    def pop(self):\n        if not self.stack:\n            return None\n        val = self.stack.pop()\n        if val == self.min_stack[-1]:\n            self.min_stack.pop()\n        return val\n\n    def min(self):\n        if not self.min_stack:\n            return None\n        return self.min_stack[-1]",
                "Usa una pila auxiliar que siempre tenga el mínimo actual en el tope. En push, si el nuevo valor es menor o igual al tope de min_stack, también se apila allí. En pop, si el valor extraído es el tope de min_stack, se desapila también.",
                800, 100, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("min-stack", "pila", "intermedio"),
                estructurasDatos, intermedio, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implementa un Trie (árbol de prefijos) con operaciones de inserción y búsqueda.",
                "class TrieNode:\n    def __init__(self):\n        self.children = {}\n        self.is_end = False\n\nclass Trie:\n    def __init__(self):\n        self.root = TrieNode()\n\n    def insert(self, word):\n        node = self.root\n        for ch in word:\n            if ch not in node.children:\n                node.children[ch] = TrieNode()\n            node = node.children[ch]\n        node.is_end = True\n\n    def search(self, word):\n        node = self.root\n        for ch in word:\n            if ch not in node.children:\n                return False\n            node = node.children[ch]\n        return node.is_end",
                "Cada nodo del Trie tiene un mapa de hijos y un marcador de fin de palabra. Insertar recorre o crea nodos para cada carácter. Buscar recorre los nodos existentes y verifica el marcador al final.",
                900, 100, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("trie", "prefijos", "intermedio"),
                estructurasDatos, intermedio, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implementa el recorrido BFS (breadth-first search) en un grafo representado como lista de adyacencia.",
                "from collections import deque\n\ndef bfs(graph, start):\n    visited = set()\n    queue = deque([start])\n    visited.add(start)\n    while queue:\n        node = queue.popleft()\n        print(node)\n        for neighbor in graph[node]:\n            if neighbor not in visited:\n                visited.add(neighbor)\n                queue.append(neighbor)",
                "Usa una cola para explorar nodos por niveles. Visita un nodo, marca como visitado, encola todos sus vecinos no visitados. Repite hasta que la cola esté vacía.",
                800, 100, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("bfs", "grafos", "intermedio"),
                estructurasDatos, intermedio, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica cómo funciona internamente un HashMap en Java. ¿Qué sucede cuando hay colisiones y cómo se redimensiona?",
                null,
                "Usa un arreglo de buckets, donde cada bucket es una lista enlazada o un árbol. La función hashCode() determina el bucket. Las colisiones se resuelven con encadenamiento: si varios elementos caen en el mismo bucket, se agregan a la lista (o árbol si la lista supera 8 elementos). Cuando el factor de carga supera 0.75, el arreglo se redimensiona al doble y se rehasdean todos los elementos.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("hashmap", "colisiones", "intermedio"),
                estructurasDatos, intermedio, admin);

        // --- Estructuras de Datos / Avanzado (5) ---
        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implementa un montículo binario (min-heap) con operaciones de insertar y extraer mínimo.",
                "class MinHeap:\n    def __init__(self):\n        self.heap = []\n\n    def insert(self, val):\n        self.heap.append(val)\n        self._bubble_up(len(self.heap) - 1)\n\n    def extract_min(self):\n        if not self.heap:\n            return None\n        self.heap[0], self.heap[-1] = self.heap[-1], self.heap[0]\n        min_val = self.heap.pop()\n        self._bubble_down(0)\n        return min_val\n\n    def _bubble_up(self, i):\n        parent = (i - 1) // 2\n        while i > 0 and self.heap[i] < self.heap[parent]:\n            self.heap[i], self.heap[parent] = self.heap[parent], self.heap[i]\n            i = parent\n            parent = (i - 1) // 2\n\n    def _bubble_down(self, i):\n        n = len(self.heap)\n        while True:\n            smallest = i\n            left = 2 * i + 1\n            right = 2 * i + 2\n            if left < n and self.heap[left] < self.heap[smallest]:\n                smallest = left\n            if right < n and self.heap[right] < self.heap[smallest]:\n                smallest = right\n            if smallest == i:\n                break\n            self.heap[i], self.heap[smallest] = self.heap[smallest], self.heap[i]\n            i = smallest",
                "Un min-heap es un árbol binario completo donde cada nodo es menor que sus hijos. Insertar agrega al final y hace bubble-up. Extraer mínimo intercambia raíz con último, elimina y hace bubble-down. Complejidad O(log n) en ambas operaciones.",
                900, 120, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("heap", "monticulo", "avanzado"),
                estructurasDatos, avanzado, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implementa un árbol de segmentos para consultas de suma en rango con actualizaciones puntuales.",
                "class SegmentTree:\n    def __init__(self, arr):\n        n = len(arr)\n        self.tree = [0] * (4 * n)\n        self.arr = arr\n        self._build(0, 0, n - 1)\n\n    def _build(self, node, start, end):\n        if start == end:\n            self.tree[node] = self.arr[start]\n        else:\n            mid = (start + end) // 2\n            self._build(2 * node + 1, start, mid)\n            self._build(2 * node + 2, mid + 1, end)\n            self.tree[node] = self.tree[2 * node + 1] + self.tree[2 * node + 2]\n\n    def query(self, node, start, end, l, r):\n        if r < start or l > end:\n            return 0\n        if l <= start and end <= r:\n            return self.tree[node]\n        mid = (start + end) // 2\n        left = self.query(2 * node + 1, start, mid, l, r)\n        right = self.query(2 * node + 2, mid + 1, end, l, r)\n        return left + right\n\n    def update(self, node, start, end, idx, val):\n        if start == end:\n            self.arr[idx] = val\n            self.tree[node] = val\n        else:\n            mid = (start + end) // 2\n            if idx <= mid:\n                self.update(2 * node + 1, start, mid, idx, val)\n            else:\n                self.update(2 * node + 2, mid + 1, end, idx, val)\n            self.tree[node] = self.tree[2 * node + 1] + self.tree[2 * node + 2]",
                "Un árbol de segmentos almacena información agregada (suma, mínimo, etc.) de segmentos del arreglo en un árbol binario. Las consultas y actualizaciones se realizan en O(log n) dividiendo recursivamente el rango.",
                1200, 150, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("segment-tree", "arbol", "avanzado"),
                estructurasDatos, avanzado, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implementa la estructura de conjuntos disjuntos (Union-Find) con compresión de camino y unión por rango.",
                "class UnionFind:\n    def __init__(self, n):\n        self.parent = list(range(n))\n        self.rank = [0] * n\n\n    def find(self, x):\n        if self.parent[x] != x:\n            self.parent[x] = self.find(self.parent[x])\n        return self.parent[x]\n\n    def union(self, x, y):\n        px, py = self.find(x), self.find(y)\n        if px == py:\n            return False\n        if self.rank[px] < self.rank[py]:\n            self.parent[px] = py\n        elif self.rank[px] > self.rank[py]:\n            self.parent[py] = px\n        else:\n            self.parent[py] = px\n            self.rank[px] += 1\n        return True",
                "Union-Find mantiene conjuntos disjuntos con dos operaciones: find (encuentra el representante) con compresión de camino, y union (fusiona dos conjuntos) usando el rango para mantener el árbol balanceado.",
                900, 120, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("union-find", "conjuntos", "avanzado"),
                estructurasDatos, avanzado, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica qué es un árbol AVL y cómo funcionan las rotaciones para mantener el balance.",
                null,
                "Un árbol AVL es un BST auto-balanceado donde la diferencia de altura entre los subárboles izquierdo y derecho de cada nodo (factor de balance) no supera 1. Cuando una inserción o eliminación rompe esta propiedad, se aplican rotaciones simples (left, right) o dobles (left-right, right-left) para restaurar el balance. Cada rotación es O(1).",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("avl", "balance", "avanzado"),
                estructurasDatos, avanzado, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implementa DFS recursivo en un grafo representado como lista de adyacencia.",
                "def dfs_recursive(graph, node, visited=None):\n    if visited is None:\n        visited = set()\n    visited.add(node)\n    print(node)\n    for neighbor in graph[node]:\n        if neighbor not in visited:\n            dfs_recursive(graph, neighbor, visited)\n    return visited\n\ndef dfs_iterative(graph, start):\n    visited = set()\n    stack = [start]\n    while stack:\n        node = stack.pop()\n        if node not in visited:\n            visited.add(node)\n            print(node)\n            for neighbor in reversed(graph[node]):\n                if neighbor not in visited:\n                    stack.append(neighbor)",
                "DFS explora un grafo profundizando primero antes de retroceder. La versión recursiva usa el call stack implícitamente. La versión iterativa usa una pila explícita para evitar recursión.",
                900, 120, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("dfs", "grafos", "avanzado"),
                estructurasDatos, avanzado, admin);

        // ─── DESARROLLO WEB ────────────────────────────────────────────────────────

        // --- Desarrollo Web / Fácil (5) ---
        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "¿Cuáles son los elementos semánticos de HTML5 y por qué son importantes?",
                null,
                "Elementos como <header>, <nav>, <main>, <section>, <article>, <aside>, y <footer> definen la estructura del documento. Mejoran la accesibilidad para lectores de pantalla, el SEO, y la mantenibilidad del código al hacer explícito el significado de cada sección.",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("html5", "semantica", "facil"),
                desarrolloWeb, facil, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica las diferencias entre CSS Flexbox y CSS Grid. ¿Cuándo usarías cada uno?",
                null,
                "Flexbox es unidimensional (una fila o columna), ideal para layouts lineales y alineación de elementos. Grid es bidimensional (filas y columnas simultáneamente), ideal para layouts completos de página. Flexbox es mejor para componentes pequeños; Grid para layouts de página completos.",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("css", "flexbox", "grid", "facil"),
                desarrolloWeb, facil, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "¿Cuál es la diferencia entre los métodos HTTP GET y POST? ¿Cuándo usarías cada uno?",
                null,
                "GET solicita datos al servidor, los parámetros van en la URL, es idempotente y puede cachearse. POST envía datos al servidor para crear/modificar recursos, los parámetros van en el cuerpo, no es idempotente ni cacheable. GET es seguro para lecturas; POST se usa para operaciones que cambian el estado del servidor.",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("http", "get", "post", "facil"),
                desarrolloWeb, facil, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica los principios del diseño responsivo (responsive design).",
                null,
                "El diseño responsivo usa rejillas flexibles, imágenes adaptables y media queries CSS para que el sitio se vea bien en cualquier dispositivo. Los principios clave incluyen: enfoque mobile-first, layouts fluidos (usando % o fr en lugar de px fijos), breakpoints basados en contenido, y pruebas en múltiples dispositivos.",
                600, 50, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("responsive", "css", "facil"),
                desarrolloWeb, facil, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "¿Qué es una cookie HTTP y cómo se usa en aplicaciones web?",
                null,
                "Una cookie es un pequeño dato que el servidor envía al navegador y que el navegador almacena y reenvía en solicitudes posteriores al mismo servidor. Se usa para mantener sesiones, rastrear preferencias de usuario, y almacenar tokens de autenticación. Tienen atributos como domain, path, expires, Secure y HttpOnly para controlar su comportamiento.",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("cookies", "http", "facil"),
                desarrolloWeb, facil, admin);

        // --- Desarrollo Web / Intermedio (5) ---
        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica las diferencias clave entre las APIs REST y GraphQL. ¿Cuándo elegirías una sobre la otra?",
                null,
                "REST usa endpoints fijos para los recursos, mientras que GraphQL usa un solo endpoint con un lenguaje de consulta flexible. REST es más simple para CRUD básico, mientras que GraphQL destaca cuando los clientes necesitan diferentes formas de datos.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("rest", "graphql", "api", "web"),
                desarrolloWeb, intermedio, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica qué es CORS (Cross-Origin Resource Sharing) y cómo se configura en el servidor.",
                null,
                "CORS es un mecanismo de seguridad del navegador que controla las solicitudes entre diferentes orígenes. El servidor debe incluir cabeceras como Access-Control-Allow-Origin en las respuestas. Para solicitudes complejas, el navegador envía una solicitud preflight OPTIONS antes de la solicitud real.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("cors", "seguridad", "intermedio"),
                desarrolloWeb, intermedio, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica el flujo de autenticación usando JWT (JSON Web Tokens).",
                null,
                "El usuario se autentica con credenciales, el servidor verifica y genera un JWT firmado (header.payload.signature). El cliente almacena el token y lo envía en cada solicitud como cabecera Authorization: Bearer <token>. El servidor verifica la firma y extrae los datos del usuario del payload. Los JWT son stateless y no requieren almacenamiento en servidor.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("jwt", "autenticacion", "intermedio"),
                desarrolloWeb, intermedio, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Compara WebSockets con SSE (Server-Sent Events). ¿Cuándo usarías cada uno?",
                null,
                "WebSockets proporcionan comunicación bidireccional full-duplex, ideal para aplicaciones en tiempo real como chats o juegos. SSE es unidireccional (servidor a cliente) sobre HTTP, más simple y con reintentos automáticos, ideal para notificaciones, actualizaciones de feeds o dashboards. WebSockets requieren un servidor especializado; SSE funciona con servidores HTTP estándar.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("websockets", "sse", "tiempo-real", "intermedio"),
                desarrolloWeb, intermedio, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "¿Qué son las PWAs (Progressive Web Apps) y cuáles son sus características principales?",
                null,
                "Las PWAs son aplicaciones web que usan tecnologías modernas para ofrecer una experiencia similar a una app nativa. Características clave: service workers para funcionamiento offline, manifest.json para instalación, notificaciones push, carga rápida, y capacidad de respuesta. Ventajas: no requieren tienda de aplicaciones, son actualizables instantáneamente, y funcionan en múltiples plataformas.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("pwa", "progressive-web-app", "intermedio"),
                desarrolloWeb, intermedio, admin);

        // --- Desarrollo Web / Avanzado (5) ---
        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica la arquitectura de microservicios. ¿Cuáles son sus ventajas y desafíos frente a una arquitectura monolítica?",
                null,
                "Los microservicios descomponen una aplicación en servicios pequeños, independientes y desplegables por separado, cada uno con su propia base de datos y comunicándose mediante APIs. Ventajas: escalado independiente, equipos autónomos, despliegue aislado, tolerancia a fallos. Desafíos: complejidad de red, consistencia de datos, orquestación, monitoreo distribuido, debugging.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("microservicios", "arquitectura", "avanzado"),
                desarrolloWeb, avanzado, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica el flujo completo de OAuth 2.0 con el grant type Authorization Code.",
                null,
                "El cliente redirige al usuario al servidor de autorización, quien autentica al usuario y solicita consentimiento. Luego redirige al cliente con un código de autorización. El cliente intercambia este código por un access token (y opcionalmente un refresh token) mediante una solicitud directa al servidor de autorización. Este grant type es el más seguro porque el token nunca pasa por el navegador del usuario.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("oauth2", "autenticacion", "avanzado"),
                desarrolloWeb, avanzado, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Compara Docker y Kubernetes. ¿Qué problema resuelve cada uno?",
                null,
                "Docker empaqueta aplicaciones en contenedores ligeros y portátiles que incluyen todo lo necesario para ejecutarse. Kubernetes orquesta contenedores en un clúster de servidores: maneja escalado automático, balanceo de carga, despliegues rolling, auto-recuperación, y descubrimiento de servicios. Docker resuelve el problema de 'funciona en mi máquina'; Kubernetes resuelve el problema de gestionar muchos contenedores en producción.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("docker", "kubernetes", "avanzado"),
                desarrolloWeb, avanzado, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica cómo funciona una CDN y qué estrategias de caché se usan para optimizar la entrega de contenido.",
                null,
                "Una CDN (Content Delivery Network) distribuye contenido estático en servidores geográficamente dispersos (edge servers). Cuando un usuario solicita contenido, se sirve desde el edge server más cercano. Estrategias de caché: TTL (time-to-live), cache invalidation por versión (fingerprinting), cache-control headers, stale-while-revalidate, y purging selectivo. Las CDN mejoran latencia, disponibilidad y reducen carga en el servidor de origen.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("cdn", "cache", "rendimiento", "avanzado"),
                desarrolloWeb, avanzado, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica las vulnerabilidades de seguridad web más comunes: XSS, CSRF y SQL Injection. ¿Cómo se mitigan?",
                null,
                "XSS (Cross-Site Scripting): inyección de scripts maliciosos. Se mitiga escapando output, usando Content-Security-Policy, y validando input. CSRF (Cross-Site Request Forgery): ataques que hacen que el usuario ejecute acciones no deseadas. Se mitiga con tokens CSRF, SameSite cookies, y verificando cabeceras de origen. SQL Injection: inyección de comandos SQL maliciosos. Se mitiga usando consultas parametrizadas/prepared statements, ORMs, y validando input.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("seguridad", "xss", "csrf", "sql-injection", "avanzado"),
                desarrolloWeb, avanzado, admin);

        // ─── BASES DE DATOS ────────────────────────────────────────────────────────

        // --- Bases de Datos / Fácil (5) ---
        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una consulta SQL que seleccione todos los usuarios activos ordenados por fecha de creación descendente.",
                "SELECT * FROM usuarios WHERE activo = true ORDER BY fecha_creacion DESC;",
                "Usa WHERE para filtrar usuarios activos y ORDER BY DESC para ordenar del más reciente al más antiguo.",
                480, 40, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("sql", "select", "facil"),
                basesDatos, facil, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una consulta SQL que use JOIN para obtener los pedidos de cada cliente, incluyendo aquellos clientes sin pedidos.",
                "SELECT c.nombre, p.id AS pedido_id, p.total\nFROM clientes c\nLEFT JOIN pedidos p ON c.id = p.cliente_id\nORDER BY c.nombre;",
                "LEFT JOIN asegura que todos los clientes aparezcan, incluso si no tienen pedidos. Los clientes sin pedidos tendrán NULL en las columnas de pedidos.",
                600, 50, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("sql", "join", "facil"),
                basesDatos, facil, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica la diferencia entre clave primaria (primary key) y clave foránea (foreign key).",
                null,
                "La clave primaria identifica de forma única cada fila de una tabla. No puede ser NULL y debe ser única. La clave foránea es un campo que referencia la clave primaria de otra tabla, estableciendo una relación entre ambas tablas. Las FK mantienen la integridad referencial: no se puede insertar un valor FK que no exista en la tabla referenciada.",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("primary-key", "foreign-key", "facil"),
                basesDatos, facil, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "¿Cuál es la diferencia entre WHERE y HAVING en SQL?",
                null,
                "WHERE filtra filas antes de la agrupación (GROUP BY), no puede usar funciones de agregación. HAVING filtra grupos después de la agrupación y puede usar funciones de agregación como COUNT(), SUM(), AVG(). Se usan juntos: WHERE primero, luego GROUP BY, luego HAVING.",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("sql", "where", "having", "facil"),
                basesDatos, facil, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una consulta SQL que calcule el total de ventas por producto usando funciones de agregación, mostrando solo productos con más de 10 unidades vendidas.",
                "SELECT p.nombre, SUM(v.cantidad) AS total_unidades, SUM(v.total) AS ventas_totales\nFROM productos p\nJOIN ventas v ON p.id = v.producto_id\nGROUP BY p.nombre\nHAVING SUM(v.cantidad) > 10\nORDER BY ventas_totales DESC;",
                "JOIN combina las tablas, GROUP BY agrupa por producto, SUM calcula los totales, HAVING filtra productos con más de 10 unidades, ORDER BY ordena de mayor a menor.",
                600, 50, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("sql", "agregacion", "facil"),
                basesDatos, facil, admin);

        // --- Bases de Datos / Intermedio (5) ---
        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una consulta SQL para encontrar direcciones de email duplicadas en una tabla de usuarios. La tabla tiene las columnas: id, name, email.",
                "SELECT email, COUNT(*) as count FROM usuarios GROUP BY email HAVING COUNT(*) > 1;",
                "Agrupa por email, cuenta las ocurrencias, filtra los grupos con count mayor a 1.",
                480, 80, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("sql", "duplicados", "bases-de-datos"),
                basesDatos, intermedio, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica cómo funcionan los índices en bases de datos y cómo afectan el rendimiento de las consultas.",
                null,
                "Los índices son estructuras de datos (generalmente B-trees) que aceleran la búsqueda de filas. Sin índice, la BD escanea toda la tabla (full scan). Con índice, puede localizar filas en O(log n). Ventajas: aceleran SELECT con WHERE, JOIN y ORDER BY. Desventajas: ralentizan INSERT/UPDATE/DELETE (hay que mantener el índice), y ocupan espacio adicional.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("indices", "rendimiento", "intermedio"),
                basesDatos, intermedio, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica el concepto de transacciones y las propiedades ACID en bases de datos.",
                null,
                "Una transacción agrupa operaciones que se ejecutan como una unidad atómica. ACID: Atomicidad (todo o nada), Consistencia (la BD siempre está en estado válido), Aislamiento (las transacciones concurrentes no interfieren), Durabilidad (los cambios persisten ante fallos). En PostgreSQL se controlan con BEGIN, COMMIT, ROLLBACK y niveles de aislamiento.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("transacciones", "acid", "intermedio"),
                basesDatos, intermedio, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica las formas normales en bases de datos relacionales (1FN, 2FN, 3FN).",
                null,
                "1FN: cada celda contiene un solo valor atómico, no hay grupos repetitivos. 2FN: está en 1FN y cada columna no clave depende completamente de la clave primaria completa (no de parte de ella). 3FN: está en 2FN y no hay dependencias transitivas (una columna no clave no depende de otra columna no clave). La normalización reduce la redundancia y mejora la integridad de los datos.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("normalizacion", "formas-normales", "intermedio"),
                basesDatos, intermedio, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una consulta SQL que use una subconsulta o CTE para encontrar empleados con salario superior al promedio de su departamento.",
                "WITH dept_avg AS (\n    SELECT departamento_id, AVG(salario) AS salario_promedio\n    FROM empleados\n    GROUP BY departamento_id\n)\nSELECT e.nombre, e.salario, d.salario_promedio\nFROM empleados e\nJOIN dept_avg d ON e.departamento_id = d.departamento_id\nWHERE e.salario > d.salario_promedio\nORDER BY e.salario DESC;",
                "El CTE calcula el salario promedio por departamento. Luego se JOINEA con empleados y se filtran aquellos con salario superior al promedio de su departamento.",
                800, 100, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("sql", "subconsulta", "cte", "intermedio"),
                basesDatos, intermedio, admin);

        // --- Bases de Datos / Avanzado (5) ---
        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica cómo optimizar consultas SQL usando EXPLAIN y analizando planes de ejecución.",
                null,
                "EXPLAIN (o EXPLAIN ANALYZE) muestra el plan de ejecución de una consulta: qué índices usa, cómo combina tablas, qué métodos de acceso emplea (seq scan, index scan, hash join, etc.). Para optimizar: buscar sequential scans en tablas grandes (agregar índices), evitar joins costosos (reescribir consultas), verificar estimaciones de filas. Herramientas como pgAdmin, EXPLAIN visualizers ayudan a interpretar.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("optimizacion", "explain", "avanzado"),
                basesDatos, avanzado, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica el concepto de sharding (fragmentación horizontal) en bases de datos. ¿Cuáles son sus ventajas y desafíos?",
                null,
                "Sharding divide una tabla grande en particiones más pequeñas (shards) distribuidas en varios servidores. Cada shard contiene un subconjunto de filas según una clave de partición (ej. hash del user_id). Ventajas: escalabilidad horizontal, mayor capacidad de almacenamiento, consultas paralelas. Desafíos: consultas cross-shard complejas, rebalanceo al agregar nodos, transacciones distribuidas, backups complejos.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("sharding", "escalabilidad", "avanzado"),
                basesDatos, avanzado, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica las diferentes estrategias de replicación en bases de datos (maestro-esclavo, multi-maestro, síncrona, asíncrona).",
                null,
                "Maestro-esclavo: un nodo principal recibe escrituras, los secundarios replican para lecturas. Multi-maestro: varios nodos aceptan escrituras. Replicación síncrona: el maestro espera confirmación de los esclavos antes de confirmar (mayor consistencia, menor rendimiento). Asíncrona: el maestro no espera confirmación (mayor rendimiento, posible pérdida de datos). PostgreSQL soporta streaming replication, logical replication y cascading replication.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("replicacion", "alta-disponibilidad", "avanzado"),
                basesDatos, avanzado, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Compara bases de datos SQL y NoSQL. ¿En qué casos elegirías cada una?",
                null,
                "SQL: esquema fijo, consistencia fuerte, relaciones y joins, ideales para datos estructurados y transacciones ACID (ej. sistemas financieros). NoSQL: esquema flexible, escalabilidad horizontal, varios modelos (documentos, clave-valor, grafos, columnas). MongoDB (documentos) para datos semiestructurados, Redis (clave-valor) para caché, Neo4j (grafos) para relaciones complejas. La elección depende de los requisitos del proyecto: consistencia vs disponibilidad, estructura vs flexibilidad.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("nosql", "sql", "bases-de-datos", "avanzado"),
                basesDatos, avanzado, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica el Teorema CAP y cómo afecta al diseño de sistemas distribuidos de bases de datos.",
                null,
                "El Teorema CAP (Consistency, Availability, Partition Tolerance) establece que un sistema distribuido solo puede garantizar dos de las tres propiedades simultáneamente. CP: consistencia y tolerancia a particiones (sacrifica disponibilidad, ej. sistemas bancarios). AP: disponibilidad y tolerancia a particiones (sacrifica consistencia, ej. redes sociales). CA: consistencia y disponibilidad (no tolera particiones, solo viable en sistemas sin particiones de red). Los sistemas modernos eligen entre CP y AP.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("teorema-cap", "distribuidos", "avanzado"),
                basesDatos, avanzado, admin);

        // ─── DISEÑO DE SISTEMAS ────────────────────────────────────────────────────

        // --- Diseño de Sistemas / Fácil (5) ---
        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica el modelo cliente-servidor. ¿Cuáles son sus componentes y cómo se comunican?",
                null,
                "El modelo cliente-servidor tiene dos componentes: el cliente (solicita servicios) y el servidor (provee servicios). La comunicación ocurre a través de la red usando protocolos como HTTP/HTTPS. El servidor escucha en un puerto, el cliente envía una solicitud, el servidor la procesa y envía una respuesta. Este modelo centraliza los recursos y permite que múltiples clientes accedan concurrentemente.",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("cliente-servidor", "arquitectura", "facil"),
                disenoSistemas, facil, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "¿Qué es un API Gateway y cuál es su función en una arquitectura de microservicios?",
                null,
                "Un API Gateway es un punto de entrada único para todas las solicitudes de los clientes a un sistema de microservicios. Funciones: enrutamiento, balanceo de carga, autenticación y autorización, limitación de tasa, transformación de protocolos, agregación de respuestas, y monitoreo. Ejemplos populares: Kong, NGINX, AWS API Gateway, Spring Cloud Gateway.",
                600, 50, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("api-gateway", "microservicios", "facil"),
                disenoSistemas, facil, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica el concepto de balanceo de carga. ¿Qué estrategias de balanceo conoces?",
                null,
                "El balanceo de carga distribuye el tráfico entre varios servidores para evitar sobrecargar uno solo. Estrategias: round robin (distribuye equitativamente), least connections (envía al servidor con menos conexiones activas), IP hash (misma IP siempre al mismo servidor), weighted round robin (asigna según capacidad). Los balanceadores pueden ser de capa 4 (transporte, basado en IP/puerto) o capa 7 (aplicación, basado en contenido HTTP).",
                600, 50, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("balanceo-carga", "escalabilidad", "facil"),
                disenoSistemas, facil, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Compara la arquitectura monolítica con la de microservicios. ¿Cuáles son las ventajas y desventajas de cada una?",
                null,
                "Monolítica: una sola aplicación que contiene toda la funcionalidad. Ventajas: simplicidad, despliegue único, monitoreo centralizado, desarrollo inicial rápido. Desventajas: escalado de todo o nada, acoplamiento, equipos dependientes, adopción tecnológica difícil. Microservicios: servicios independientes. Ventajas: escalado granular, equipos autónomos, despliegue independiente, diversidad tecnológica. Desventajas: complejidad operativa, latencia de red, consistencia eventual.",
                600, 50, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("monolitico", "microservicios", "facil"),
                disenoSistemas, facil, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica la diferencia entre escalabilidad vertical y horizontal.",
                null,
                "Escalabilidad vertical (scale up): agregar más recursos al mismo servidor (más RAM, CPU, disco). Es simple pero tiene límites físicos y puede tener downtime. Escalabilidad horizontal (scale out): agregar más servidores al sistema. Es más compleja (requiere balanceo de carga, consistencia distribuida) pero permite escalar casi infinitamente y es más tolerante a fallos. La mayoría de sistemas modernos escalan horizontalmente.",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("escalabilidad", "vertical", "horizontal", "facil"),
                disenoSistemas, facil, admin);

        // --- Diseño de Sistemas / Intermedio (5) ---
        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Diseña un sistema de chat en tiempo real como WhatsApp o Messenger. Describe la arquitectura, API y consideraciones de escalabilidad.",
                null,
                "Arquitectura: WebSocket servers para comunicación en tiempo real, message brokers (Kafka/RabbitMQ) para encolar mensajes, base de datos para persistencia (cassandra para mensajes, postgres para usuarios). API: sendMessage, getConversations, getMessages. Consideraciones: sharding por user_id, caché de conversaciones recientes, entrega de mensajes offline (push notifications), cifrado extremo a extremo, consistencia eventual para mensajes.",
                900, 100, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("chat", "tiempo-real", "intermedio"),
                disenoSistemas, intermedio, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica las estrategias de caché más comunes usando Redis. ¿Qué son la caché lateral, la escritura directa y la escritura diferida?",
                null,
                "Cache-aside (lazy loading): la aplicación consulta caché primero; si falla, busca en BD y llena el caché. Write-through: la aplicación escribe en ambos simultáneamente (mayor consistencia, mayor latencia de escritura). Write-behind (write-back): la aplicación escribe en caché, que asíncronamente persiste en BD (menor latencia, riesgo de pérdida de datos). Redis es ideal por su baja latencia, estructuras de datos ricas, y opciones de persistencia (RDB, AOF).",
                800, 100, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("redis", "cache", "intermedio"),
                disenoSistemas, intermedio, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica cómo funcionan las colas de mensajes (message queues). ¿Qué papel juegan en sistemas distribuidos?",
                null,
                "Las colas de mensajes permiten comunicación asíncrona entre servicios mediante un intermediario (broker). El productor envía mensajes a la cola, el consumidor los procesa cuando puede. Beneficios: desacoplamiento, buffering de picos de tráfico, tolerancia a fallos (mensajes persisten si el consumidor falla), procesamiento asíncrono. Ejemplos: RabbitMQ (AMQP), Apache Kafka (streaming), AWS SQS. Patrones: work queues, publish/subscribe, routing por topic.",
                800, 100, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("message-queue", "rabbitmq", "kafka", "intermedio"),
                disenoSistemas, intermedio, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Diseña un sistema de CDN (Content Delivery Network). Explica cómo se almacena y distribuye el contenido globalmente.",
                null,
                "Una CDN global tiene edge servers en múltiples ubicaciones geográficas. Flujo: 1. Usuario solicita contenido, 2. DNS resuelve al edge server más cercano (Anycast), 3. Si el edge tiene el contenido en caché, lo sirve (cache hit). 4. Si no (cache miss), lo solicita al origin server, lo almacena localmente (con TTL configurable), y lo sirve al usuario. Estrategias: push (pre-cargar contenido) vs pull (cargar bajo demanda), purging por API, versionado de assets.",
                900, 100, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("cdn", "distribucion", "intermedio"),
                disenoSistemas, intermedio, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica cómo funciona la indexación en bases de datos y cómo diseñar índices efectivos para mejorar el rendimiento.",
                null,
                "Los índices son estructuras (B-tree, Hash, GiST, GIN) que aceleran la búsqueda. Para diseñar índices efectivos: indexar columnas usadas en WHERE y JOIN, considerar índices compuestos (orden de columnas importa: las más selectivas primero), evitar índices en columnas con baja cardinalidad (ej. booleanos), usar índices parciales (WHERE condition), monitorear índices no usados. PostgreSQL ofrece EXPLAIN para verificar si se usan los índices.",
                800, 100, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("indexacion", "rendimiento", "intermedio"),
                disenoSistemas, intermedio, admin);

        // --- Diseño de Sistemas / Avanzado (5) ---
        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Diseña un servicio de acortamiento de URLs como TinyURL. Explica tus decisiones de diseño incluyendo el esquema de base de datos, diseño de API y consideraciones de escalabilidad.",
                null,
                "Usa una función hash (ej. codificación Base62 de un ID único) para generar códigos cortos. Almacena los mapeos en una BD relacional con el código corto como clave primaria. Cachea las búsquedas frecuentes con Redis. Usa un contador distribuido (ej. Snowflake) para generación de IDs únicos en múltiples servidores.",
                1200, 150, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.5, \"designWeight\": 0.3}",
                List.of("diseno-de-sistemas", "escalabilidad", "acortador-urls"),
                disenoSistemas, avanzado, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Diseña un limitador de tasa (rate limiter) para una API REST. Explica los algoritmos y la arquitectura.",
                null,
                "Algoritmos: Token Bucket (tokens que se regeneran a tasa constante), Leaky Bucket (cola de capacidad fija), Fixed Window (contador por ventana de tiempo), Sliding Window Log (registro de timestamps deslizante), Sliding Window Counter (híbrido eficiente). Arquitectura: Redis para almacenar contadores (INCR + EXPIRE), middleware en el API Gateway, respuesta 429 Too Many Requests con cabeceras RateLimit-Remaining. Distribuido: usar Redis Cluster o implementaciones locales con sincronización aproximada.",
                1200, 150, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.5, \"designWeight\": 0.3}",
                List.of("rate-limiter", "diseno-de-sistemas", "avanzado"),
                disenoSistemas, avanzado, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica el concepto de hashing consistente (consistent hashing). ¿Cómo se usa en sistemas distribuidos como Cassandra o DynamoDB?",
                null,
                "El hashing consistente mapea cada nodo a múltiples puntos en un anillo hash usando varias funciones hash (réplicas virtuales). Cada clave se asigna al siguiente nodo en el anillo. Cuando se agrega o elimina un nodo, solo las claves de ese nodo adyacente necesitan reasignarse (en lugar de rehashear todas). Se usa en Cassandra (particionamiento), DynamoDB, CDNs, y sistemas de caché distribuida como Memcached. Minimiza la reorganización de datos al escalar.",
                1200, 150, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.5, \"designWeight\": 0.3}",
                List.of("consistent-hashing", "distribuidos", "avanzado"),
                disenoSistemas, avanzado, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Diseña un almacén clave-valor distribuido como DynamoDB o Cassandra. Explica la partición, replicación y consistencia.",
                null,
                "Partición: hashing consistente con réplicas virtuales distribuye los datos entre nodos. Replicación: cada clave se replica en N nodos (factor de replicación), el coordinador escribe en todas las réplicas. Consistencia: quorum (R + W > N para lecturas/escrituras fuertemente consistentes), eventual (lecturas pueden devolver datos obsoletos). Gossip protocol para descubrimiento, hinted handoff para tolerancia a fallos, hinted replication para reparación. Merge de conflictos usando vectores de versión o LWW (last-write-wins).",
                1200, 150, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.5, \"designWeight\": 0.3}",
                List.of("key-value-store", "distribuidos", "avanzado"),
                disenoSistemas, avanzado, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Diseña un sistema de leaderboard en tiempo real para un videojuego online con millones de jugadores.",
                null,
                "Usar Redis Sorted Sets (ZADD para puntuaciones, ZRANK para ranking, ZREVRANGE para top N). Redis maneja millones de operaciones/s por nodo. Para escalar: shard por rango de IDs (ej. hash del user_id), agregar resultados globales periódicamente con jobs asíncronos. Caché del top 100 actualizada cada 5s. Para puntuaciones en tiempo real, websocket push al cliente. BD relacional como capa de persistencia y consultas históricas. Evitar consultas en tiempo real a la BD principal.",
                1200, 150, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.5, \"designWeight\": 0.3}",
                List.of("leaderboard", "tiempo-real", "avanzado"),
                disenoSistemas, avanzado, admin);

        log.info("Seed complete — {} users, {} difficulty levels, {} categories, {} questions, {} achievements",
                userRepo.count(), difficultyLevelRepo.count(), categoryRepo.count(), questionRepo.count(),
                achievementRepo.count());
    }

    private DifficultyLevel createDifficultyLevel(String name, String slug, int order, BigDecimal multiplier, String desc) {
        var dl = new DifficultyLevel();
        dl.setName(name);
        dl.setSlug(slug);
        dl.setLevelOrder(order);
        dl.setPointsMultiplier(multiplier);
        dl.setDescription(desc);
        return difficultyLevelRepo.save(dl);
    }

    private Category createCategory(String name, String slug, String desc, String icon, int order) {
        var c = new Category();
        c.setName(name);
        c.setSlug(slug);
        c.setDescription(desc);
        c.setIcon(icon);
        c.setDisplayOrder(order);
        c.setIsActive(true);
        return categoryRepo.save(c);
    }

    private InterviewType createInterviewType(String name, String slug, QuestionType qtype, int totalQ, int totalTime, String desc) {
        var it = new InterviewType();
        it.setName(name);
        it.setSlug(slug);
        it.setQuestionType(qtype);
        it.setTotalQuestions(totalQ);
        it.setTotalTimeSeconds(totalTime);
        it.setDescription(desc);
        it.setIsActive(true);
        return interviewTypeRepo.save(it);
    }

    private EvaluationCriterion createEvaluationCriterion(String name, String slug, String desc, BigDecimal weight) {
        var ec = new EvaluationCriterion();
        ec.setName(name);
        ec.setSlug(slug);
        ec.setDescription(desc);
        ec.setDefaultWeight(weight);
        ec.setIsActive(true);
        return evaluationCriterionRepo.save(ec);
    }

    private User createAdminUser(DifficultyLevel level) {
        var admin = new User();
        admin.setEmail("admin@devmock.com");
        admin.setPasswordHash(passwordEncoder.encode("Admin123!"));
        admin.setFullName("Admin DevMock");
        admin.setRole(UserRole.ADMIN);
        admin.setIsActive(true);
        admin.setIsVerified(true);
        admin.setCurrentLevel(level);
        return userRepo.save(admin);
    }

    private Achievement createAchievement(String name, String slug, String desc, String criteria, int points) {
        var a = new Achievement();
        a.setName(name);
        a.setSlug(slug);
        a.setDescription(desc);
        a.setUnlockCriteria(criteria);
        a.setPointsReward(points);
        a.setIsActive(true);
        return achievementRepo.save(a);
    }

    private void createQuestion(QuestionType qtype, AnswerFormat fmt, String statement, String answer, String explanation,
            int estimatedTime, int basePoints, String evalConfig, List<String> tags,
            Category category, DifficultyLevel difficulty, User createdBy) {

        var q = new Question();
        q.setQuestionType(qtype);
        q.setAnswerFormat(fmt);
        q.setStatement(statement);
        q.setExpectedAnswer(answer);
        q.setExplanation(explanation);
        q.setEstimatedTimeSeconds(estimatedTime);
        q.setBasePoints(basePoints);
        q.setEvaluationConfig(evalConfig);
        q.setTags(tags);
        q.setCategory(category);
        q.setDifficulty(difficulty);
        q.setCreatedBy(createdBy);
        q.setIsActive(true);
        questionRepo.save(q);
    }
}
