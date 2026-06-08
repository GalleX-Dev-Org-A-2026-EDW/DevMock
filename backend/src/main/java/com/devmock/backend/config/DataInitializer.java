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
import com.devmock.backend.entity.AnswerOption;
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

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.MULTIPLE_CHOICE,
                "¿Cuáles de los siguientes son tipos de datos primitivos en Java?",
                "int, boolean y double son primitivos. String y Array son tipos de referencia.",
                "Los tipos primitivos en Java son: byte, short, int, long, float, double, boolean y char. String es una clase, Array es un tipo de referencia.",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("java", "tipos", "primitivos"),
                algoritmos, facil, admin,
                option("int", true, "Correcto: int es un tipo primitivo.", 1),
                option("String", false, "Incorrecto: String es una clase, no un tipo primitivo.", 2),
                option("boolean", true, "Correcto: boolean es un tipo primitivo.", 3),
                option("double", true, "Correcto: double es un tipo primitivo.", 4),
                option("Array", false, "Incorrecto: Array es un tipo de referencia.", 5));

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.SINGLE_CHOICE,
                "¿Cuál es la complejidad temporal de la búsqueda binaria?",
                "O(log n)",
                "La búsqueda binaria divide el espacio de búsqueda a la mitad en cada iteración, resultando en complejidad O(log n).",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("busqueda-binaria", "complejidad", "algoritmos"),
                algoritmos, facil, admin,
                option("O(n)", false, "Incorrecto: esa es la complejidad de la búsqueda lineal.", 1),
                option("O(log n)", true, "Correcto: cada iteración divide el espacio a la mitad.", 2),
                option("O(n²)", false, "Incorrecto: esa es la complejidad de Bubble Sort.", 3),
                option("O(1)", false, "Incorrecto: el acceso a un array por índice es O(1), no la búsqueda binaria.", 4));

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

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.MULTIPLE_CHOICE,
                "¿Cuáles de los siguientes algoritmos de ordenamiento tienen complejidad O(n log n) en el caso promedio?",
                "Merge Sort, Quick Sort y Heap Sort tienen complejidad O(n log n) en promedio.",
                "Merge Sort: O(n log n) siempre. Quick Sort: O(n log n) promedio, O(n²) peor caso. Heap Sort: O(n log n) siempre. Bubble Sort e Insertion Sort son O(n²).",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("ordenamiento", "complejidad", "algoritmos"),
                algoritmos, intermedio, admin,
                option("Bubble Sort", false, "Incorrecto: Bubble Sort es O(n²) en promedio.", 1),
                option("Merge Sort", true, "Correcto: Merge Sort es O(n log n) en todos los casos.", 2),
                option("Quick Sort", true, "Correcto: Quick Sort es O(n log n) en promedio.", 3),
                option("Insertion Sort", false, "Incorrecto: Insertion Sort es O(n²) en promedio.", 4),
                option("Heap Sort", true, "Correcto: Heap Sort es O(n log n) en todos los casos.", 5));

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.SINGLE_CHOICE,
                "¿Qué estructura de datos usa el algoritmo DFS (Depth-First Search) para su implementación iterativa?",
                "Pila (stack)",
                "DFS usa una pila (stack) porque explora profundizando primero, y la pila permite retroceder al último nodo visitado. BFS usa una cola.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("dfs", "grafos", "estructuras-de-datos"),
                algoritmos, intermedio, admin,
                option("Cola (queue)", false, "Incorrecto: la cola se usa para BFS.", 1),
                option("Pila (stack)", true, "Correcto: DFS iterativo usa una pila explícita.", 2),
                option("Lista enlazada", false, "Incorrecto: las listas enlazadas no se usan directamente para DFS.", 3),
                option("Árbol binario", false, "Incorrecto: un árbol binario es una estructura de datos, no un mecanismo de recorrido.", 4));

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

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.MULTIPLE_CHOICE,
                "¿Cuáles son las propiedades fundamentales que debe tener un problema para resolverse con programación dinámica?",
                "Subestructura óptima y superposición de subproblemas.",
                "Subestructura óptima: la solución óptima del problema contiene soluciones óptimas de sus subproblemas. Superposición de subproblemas: los mismos subproblemas se resuelven múltiples veces. Divide y vencerás no requiere superposición, y greedy no siempre da la solución óptima.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("programacion-dinamica", "algoritmos", "avanzado"),
                algoritmos, avanzado, admin,
                option("Subestructura óptima", true, "Correcto: la solución óptima contiene sub-soluciones óptimas.", 1),
                option("Superposición de subproblemas", true, "Correcto: los mismos subproblemas aparecen repetidamente.", 2),
                option("Divide y vencerás", false, "Incorrecto: divide y vencerás no requiere superposición de subproblemas.", 3),
                option("Greedy siempre óptimo", false, "Incorrecto: greedy no siempre encuentra la solución óptima global.", 4));

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.SINGLE_CHOICE,
                "¿Cuál es la complejidad temporal del algoritmo de Dijkstra cuando se implementa con un heap binario?",
                "O((V + E) log V)",
                "Con un heap binario, cada extracción del mínimo es O(log V) y cada relajación de arista es O(log V), dando O((V+E) log V) donde V son vértices y E son aristas.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("dijkstra", "grafos", "complejidad"),
                algoritmos, avanzado, admin,
                option("O(V²)", false, "Incorrecto: esa es la complejidad con un array simple.", 1),
                option("O((V + E) log V)", true, "Correcto: con heap binario esta es la complejidad óptima.", 2),
                option("O(V + E)", false, "Incorrecto: esa sería la complejidad de un recorrido simple como BFS.", 3),
                option("O(2^V)", false, "Incorrecto: esa complejidad es exponencial, no aplica aquí.", 4));

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

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.MULTIPLE_CHOICE,
                "¿Cuáles de las siguientes son operaciones con complejidad O(1) en una lista enlazada simple?",
                "Insertar al inicio y eliminar al inicio son O(1).",
                "Insertar al inicio: solo se actualiza la cabeza, O(1). Eliminar al inicio: misma razón. Buscar un elemento: requiere recorrer, O(n). Acceder por índice: requiere recorrer, O(n). Insertar al final: requiere llegar al último nodo, O(n).",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("lista-enlazada", "complejidad", "facil"),
                estructurasDatos, facil, admin,
                option("Insertar al inicio", true, "Correcto: solo se actualiza el puntero head.", 1),
                option("Buscar un elemento", false, "Incorrecto: requiere recorrer la lista, O(n).", 2),
                option("Eliminar al inicio", true, "Correcto: solo se mueve el puntero head.", 3),
                option("Acceder por índice", false, "Incorrecto: las listas no tienen acceso aleatorio.", 4));

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.SINGLE_CHOICE,
                "¿Qué complejidad temporal tiene la búsqueda de un elemento en una tabla hash sin colisiones?",
                "O(1)",
                "En una tabla hash sin colisiones, la función hash calcula el índice directamente y se accede al elemento en tiempo constante O(1).",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("hash-table", "complejidad", "facil"),
                estructurasDatos, facil, admin,
                option("O(1)", true, "Correcto: el acceso a una tabla hash es O(1) en el caso promedio sin colisiones.", 1),
                option("O(log n)", false, "Incorrecto: esa sería la complejidad de un árbol balanceado.", 2),
                option("O(n)", false, "Incorrecto: esa sería la complejidad de una búsqueda lineal.", 3),
                option("O(n²)", false, "Incorrecto: esa complejidad no es relevante aquí.", 4));

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

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.MULTIPLE_CHOICE,
                "¿Cuáles de los siguientes son usos comunes de la estructura de datos Trie?",
                "Autocompletado, corrector ortográfico y búsqueda por prefijos.",
                "Un Trie es ideal para búsquedas por prefijo, autocompletado, correctores ortográficos y router de IPs. No se usa típicamente para ordenar elementos (heap), ni para colas de prioridad (heap).",
                800, 100, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("trie", "prefijos", "intermedio"),
                estructurasDatos, intermedio, admin,
                option("Autocompletado", true, "Correcto: los Tries son excelentes para autocompletado.", 1),
                option("Corrector ortográfico", true, "Correcto: los Tries permiten verificar palabras rápidamente.", 2),
                option("Cola de prioridad", false, "Incorrecto: las colas de prioridad usan heaps, no Tries.", 3),
                option("Búsqueda por prefijos", true, "Correcto: los Tries están optimizados para búsquedas por prefijo.", 4));

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.SINGLE_CHOICE,
                "¿Qué estructura de datos se usa principalmente en el algoritmo BFS?",
                "Cola (queue)",
                "BFS explora el grafo nivel por nivel usando una cola. Los nodos se encolan cuando se descubren y se desencolan para procesarlos, garantizando que se visiten en orden de distancia desde el origen.",
                800, 100, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("bfs", "grafos", "intermedio"),
                estructurasDatos, intermedio, admin,
                option("Pila (stack)", false, "Incorrecto: la pila se usa para DFS, no para BFS.", 1),
                option("Cola (queue)", true, "Correcto: BFS usa una cola para el recorrido por niveles.", 2),
                option("Lista enlazada", false, "Incorrecto: las listas enlazadas no son la estructura principal de BFS.", 3),
                option("Árbol binario", false, "Incorrecto: BFS no usa árboles binarios.", 4));

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

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.MULTIPLE_CHOICE,
                "¿En cuáles de los siguientes problemas es aplicable la estructura Union-Find?",
                "Detección de ciclos en grafos, Kruskal y conectividad de componentes.",
                "Union-Find se usa para: detectar ciclos en grafos no dirigidos, algoritmo de Kruskal para MST, determinar componentes conectados, y redes de computadoras. No se usa para caminos más cortos (Dijkstra) ni para ordenamiento de arreglos.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("union-find", "grafos", "avanzado"),
                estructurasDatos, avanzado, admin,
                option("Detectar ciclos en un grafo", true, "Correcto: Union-Find detecta ciclos en grafos no dirigidos.", 1),
                option("Algoritmo de Kruskal", true, "Correcto: Kruskal usa Union-Find para construir el MST.", 2),
                option("Camino más corto", false, "Incorrecto: Dijkstra o Bellman-Ford resuelven caminos más cortos.", 3),
                option("Componentes conectados", true, "Correcto: Union-Find determina componentes en grafos.", 4));

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.SINGLE_CHOICE,
                "¿Cuál es el factor de balance máximo permitido en un árbol AVL?",
                "1",
                "En un árbol AVL, la diferencia de altura entre el subárbol izquierdo y derecho de cualquier nodo no puede superar 1. Si el factor de balance es -1, 0 o 1, el árbol está balanceado.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("avl", "balance", "avanzado"),
                estructurasDatos, avanzado, admin,
                option("0", false, "Incorrecto: 0 sería un árbol perfectamente balanceado, pero AVL permite diferencia de 1.", 1),
                option("1", true, "Correcto: el factor de balance debe ser -1, 0 o 1.", 2),
                option("2", false, "Incorrecto: si la diferencia es 2, el árbol está desbalanceado y requiere rotación.", 3),
                option("log n", false, "Incorrecto: log n es la altura máxima, no el factor de balance.", 4));

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

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.MULTIPLE_CHOICE,
                "¿Cuáles de los siguientes son métodos HTTP válidos?",
                "GET, POST, PUT, DELETE, PATCH son métodos HTTP válidos.",
                "Los métodos HTTP principales son: GET (leer), POST (crear), PUT (reemplazar), PATCH (actualizar parcial), DELETE (eliminar), HEAD, OPTIONS, TRACE, CONNECT. SAVE y FETCH no son métodos HTTP.",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("http", "metodos", "facil"),
                desarrolloWeb, facil, admin,
                option("GET", true, "Correcto: GET es un método HTTP estándar para recuperar datos.", 1),
                option("SAVE", false, "Incorrecto: SAVE no es un método HTTP.", 2),
                option("DELETE", true, "Correcto: DELETE es un método HTTP para eliminar recursos.", 3),
                option("PATCH", true, "Correcto: PATCH se usa para actualizaciones parciales.", 4));

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.SINGLE_CHOICE,
                "¿Qué técnica CSS se usa principalmente para crear diseños responsivos que se adapten a diferentes tamaños de pantalla?",
                "Media queries",
                "Las media queries en CSS permiten aplicar estilos condicionales según el tamaño de la pantalla, resolución, orientación, etc. Son la base del diseño responsivo junto con unidades relativas y grids flexibles.",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("responsive", "css", "facil"),
                desarrolloWeb, facil, admin,
                option("Variables CSS", false, "Incorrecto: las variables CSS son para reutilizar valores, no para diseño responsivo.", 1),
                option("Media queries", true, "Correcto: las media queries son esenciales para el diseño responsivo.", 2),
                option("Flexbox", false, "Incorrecto: Flexbox es un modelo de layout, no una técnica de adaptación por sí mismo.", 3),
                option("Animaciones CSS", false, "Incorrecto: las animaciones no están relacionadas con diseño responsivo.", 4));

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

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.MULTIPLE_CHOICE,
                "¿Cuáles de los siguientes son mecanismos de seguridad en aplicaciones web?",
                "CORS, CSP y SameSite cookies son mecanismos de seguridad web.",
                "CORS controla solicitudes entre orígenes. CSP (Content Security Policy) previene XSS. SameSite cookies protege contra CSRF. TCP es un protocolo de transporte, no de seguridad. SQL es un lenguaje de consulta.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("seguridad", "cors", "csp", "intermedio"),
                desarrolloWeb, intermedio, admin,
                option("CORS", true, "Correcto: CORS controla el acceso entre diferentes orígenes.", 1),
                option("CSP", true, "Correcto: Content Security Policy previene ataques XSS.", 2),
                option("TCP", false, "Incorrecto: TCP es un protocolo de la capa de transporte, no de seguridad web.", 3),
                option("SameSite cookies", true, "Correcto: SameSite protege contra ataques CSRF.", 4));

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica el flujo de autenticación usando JWT (JSON Web Tokens).",
                null,
                "El usuario se autentica con credenciales, el servidor verifica y genera un JWT firmado (header.payload.signature). El cliente almacena el token y lo envía en cada solicitud como cabecera Authorization: Bearer <token>. El servidor verifica la firma y extrae los datos del usuario del payload. Los JWT son stateless y no requieren almacenamiento en servidor.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("jwt", "autenticacion", "intermedio"),
                desarrolloWeb, intermedio, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.SINGLE_CHOICE,
                "¿Qué tecnología permite comunicación bidireccional en tiempo real entre cliente y servidor?",
                "WebSockets",
                "WebSockets proporcionan comunicación bidireccional full-duplex, donde tanto el cliente como el servidor pueden enviar mensajes en cualquier momento. SSE es unidireccional (solo servidor a cliente).",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("websockets", "tiempo-real", "intermedio"),
                desarrolloWeb, intermedio, admin,
                option("SSE", false, "Incorrecto: SSE es unidireccional, solo servidor a cliente.", 1),
                option("WebSockets", true, "Correcto: WebSockets permite comunicación bidireccional full-duplex.", 2),
                option("HTTP Long Polling", false, "Incorrecto: el long polling simula tiempo real pero no es bidireccional verdadero.", 3),
                option("REST", false, "Incorrecto: REST es un estilo de arquitectura, no de comunicación en tiempo real.", 4));

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

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.MULTIPLE_CHOICE,
                "¿Cuáles de los siguientes son componentes del flujo OAuth 2.0?",
                "Resource owner, client, authorization server y resource server.",
                "Los cuatro actores principales de OAuth 2.0 son: resource owner (usuario), client (aplicación), authorization server (emite tokens), y resource server (aloja los recursos protegidos). JWT es un formato de token, no un actor.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("oauth2", "autenticacion", "seguridad"),
                desarrolloWeb, avanzado, admin,
                option("Resource owner", true, "Correcto: el usuario propietario de los recursos.", 1),
                option("JWT", false, "Incorrecto: JWT es un formato de token, no un componente de OAuth 2.0.", 2),
                option("Authorization server", true, "Correcto: el servidor que emite los tokens.", 3),
                option("Resource server", true, "Correcto: el servidor que aloja los recursos protegidos.", 4));

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Compara Docker y Kubernetes. ¿Qué problema resuelve cada uno?",
                null,
                "Docker empaqueta aplicaciones en contenedores ligeros y portátiles que incluyen todo lo necesario para ejecutarse. Kubernetes orquesta contenedores en un clúster de servidores: maneja escalado automático, balanceo de carga, despliegues rolling, auto-recuperación, y descubrimiento de servicios. Docker resuelve el problema de 'funciona en mi máquina'; Kubernetes resuelve el problema de gestionar muchos contenedores en producción.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("docker", "kubernetes", "avanzado"),
                desarrolloWeb, avanzado, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.SINGLE_CHOICE,
                "¿Qué protocolo de resolución de nombres se usa para dirigir al usuario al servidor CDN más cercano?",
                "Anycast DNS",
                "Anycast DNS permite que múltiples servidores compartan la misma dirección IP, y el tráfico se enruta al servidor más cercano geográficamente. Round Robin DNS distribuye entre varias IPs pero no considera cercanía.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("cdn", "dns", "anycast"),
                desarrolloWeb, avanzado, admin,
                option("Round Robin DNS", false, "Incorrecto: Round Robin distribuye tráfico entre IPs pero no dirige al más cercano.", 1),
                option("Anycast DNS", true, "Correcto: Anycast enruta al servidor más cercano disponible.", 2),
                option("HTTP Redirect", false, "Incorrecto: HTTP Redirect requiere una solicitud adicional del cliente.", 3),
                option("DNS CNAME", false, "Incorrecto: CNAME es un alias de dominio, no resuelve cercanía geográfica.", 4));

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

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.MULTIPLE_CHOICE,
                "¿Cuáles de las siguientes afirmaciones sobre claves primarias son correctas?",
                "Una clave primaria identifica unívocamente cada fila, no puede ser NULL y debe ser única.",
                "La clave primaria (PK) identifica cada fila de forma única. No puede contener NULL y todos sus valores deben ser únicos. Una tabla puede tener una sola PK, que puede ser simple o compuesta. La FK referencia la PK de otra tabla.",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("primary-key", "bases-de-datos", "facil"),
                basesDatos, facil, admin,
                option("Identifica unívocamente cada fila", true, "Correcto: la PK es el identificador único de cada registro.", 1),
                option("Puede contener valores NULL", false, "Incorrecto: una PK no puede contener NULL por definición.", 2),
                option("Debe ser única en la tabla", true, "Correcto: todos los valores de la PK deben ser únicos.", 3),
                option("Una tabla puede tener varias PK", false, "Incorrecto: una tabla puede tener solo una PK (puede ser compuesta).", 4));

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.SINGLE_CHOICE,
                "¿En qué orden se ejecutan las cláusulas en una consulta SQL con GROUP BY?",
                "WHERE → GROUP BY → HAVING",
                "El orden correcto de ejecución es: FROM, WHERE, GROUP BY, HAVING, SELECT, ORDER BY. WHERE filtra filas individuales antes de agrupar, HAVING filtra grupos después de GROUP BY.",
                480, 40, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("sql", "group-by", "having"),
                basesDatos, facil, admin,
                option("GROUP BY → HAVING → WHERE", false, "Incorrecto: WHERE se ejecuta antes de GROUP BY.", 1),
                option("WHERE → GROUP BY → HAVING", true, "Correcto: WHERE filtra filas, luego GROUP BY agrupa, luego HAVING filtra grupos.", 2),
                option("HAVING → WHERE → GROUP BY", false, "Incorrecto: HAVING requiere que GROUP BY ya haya ocurrido.", 3),
                option("GROUP BY → WHERE → HAVING", false, "Incorrecto: WHERE debe ejecutarse antes de GROUP BY.", 4));

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

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.MULTIPLE_CHOICE,
                "¿Cuáles son las propiedades ACID de las transacciones en bases de datos?",
                "Atomicity, Consistency, Isolation y Durability.",
                "ACID: Atomicidad (todo o nada), Consistencia (estado válido siempre), Aislamiento (transacciones concurrentes no interfieren), Durabilidad (los cambios persisten ante fallos). Escalabilidad y Disponibilidad no son propiedades ACID.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("transacciones", "acid", "intermedio"),
                basesDatos, intermedio, admin,
                option("Atomicidad", true, "Correcto: la transacción se ejecuta completamente o no se ejecuta.", 1),
                option("Escalabilidad", false, "Incorrecto: la escalabilidad no es una propiedad ACID.", 2),
                option("Aislamiento", true, "Correcto: las transacciones no interfieren entre sí.", 3),
                option("Disponibilidad", false, "Incorrecto: la disponibilidad no es parte de ACID.", 4));

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.SINGLE_CHOICE,
                "¿Qué forma normal requiere que cada columna no clave dependa completamente de la clave primaria completa?",
                "2FN (Segunda Forma Normal)",
                "La 2FN requiere que el esquema esté en 1FN y que cada columna no clave dependa de toda la clave primaria (no solo de parte de ella). Esto aplica principalmente a claves primarias compuestas.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("normalizacion", "formas-normales", "intermedio"),
                basesDatos, intermedio, admin,
                option("1FN", false, "Incorrecto: 1FN solo requiere valores atómicos.", 1),
                option("2FN", true, "Correcto: 2FN elimina dependencias parciales de la clave.", 2),
                option("3FN", false, "Incorrecto: 3FN elimina dependencias transitivas, asumiendo que ya se cumple 2FN.", 3),
                option("BCFN", false, "Incorrecto: BCFN es una forma más estricta que 3FN.", 4));

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

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.MULTIPLE_CHOICE,
                "¿Cuáles de las siguientes son ventajas de la replicación en bases de datos?",
                "Alta disponibilidad, balanceo de lecturas y tolerancia a fallos.",
                "La replicación mejora la disponibilidad (si un nodo falla, otro responde), permite distribuir lecturas entre réplicas (balanceo), y proporciona tolerancia a fallos. No elimina la necesidad de backups (son complementarios) ni acelera las escrituras (puede ralentizarlas en replicación síncrona).",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("replicacion", "alta-disponibilidad", "avanzado"),
                basesDatos, avanzado, admin,
                option("Alta disponibilidad", true, "Correcto: si un nodo falla, otro puede tomar su lugar.", 1),
                option("Elimina la necesidad de backups", false, "Incorrecto: los backups son necesarios aunque haya replicación.", 2),
                option("Balanceo de lecturas", true, "Correcto: las réplicas pueden servir consultas de solo lectura.", 3),
                option("Tolerancia a fallos", true, "Correcto: el sistema sigue funcionando si un nodo falla.", 4));

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.SINGLE_CHOICE,
                "¿Qué propiedad de las bases de datos SQL es la más importante para sistemas financieros?",
                "Consistencia fuerte (ACID)",
                "Los sistemas financieros requieren consistencia fuerte: cada transacción debe dejar los datos en un estado válido y cualquier lectura debe reflejar las escrituras más recientes. Esto lo garantizan las propiedades ACID de las bases de datos SQL.",
                900, 120, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("sql", "nosql", "acid"),
                basesDatos, avanzado, admin,
                option("Escalabilidad horizontal", false, "Incorrecto: los sistemas financieros priorizan consistencia sobre escalabilidad.", 1),
                option("Esquema flexible", false, "Incorrecto: los sistemas financieros requieren esquemas estrictos y validados.", 2),
                option("Consistencia fuerte (ACID)", true, "Correcto: la integridad de los datos es crítica en finanzas.", 3),
                option("Alta velocidad de escritura", false, "Incorrecto: la velocidad es secundaria frente a la consistencia.", 4));

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

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.MULTIPLE_CHOICE,
                "¿Cuáles de los siguientes son algoritmos de balanceo de carga?",
                "Round Robin, Least Connections e IP Hash son algoritmos de balanceo.",
                "Round Robin: distribuye equitativamente. Least Connections: envía al servidor con menos conexiones. IP Hash: misma IP siempre al mismo servidor. Thread Pool y Quick Sort no son algoritmos de balanceo de carga.",
                600, 50, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("balanceo-carga", "algoritmos", "facil"),
                disenoSistemas, facil, admin,
                option("Round Robin", true, "Correcto: Round Robin distribuye las solicitudes secuencialmente.", 1),
                option("Thread Pool", false, "Incorrecto: Thread Pool es un patrón de concurrencia, no de balanceo de carga.", 2),
                option("Least Connections", true, "Correcto: envía tráfico al servidor con menos conexiones activas.", 3),
                option("IP Hash", true, "Correcto: la IP del cliente determina el servidor destino.", 4));

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.SINGLE_CHOICE,
                "¿Cuál de las siguientes es una desventaja de la arquitectura de microservicios?",
                "Complejidad operativa y de despliegue.",
                "Los microservicios aumentan la complejidad operativa: necesitas orquestación, monitoreo distribuido, manejo de consistencia eventual, comunicación por red, y despliegue coordinado. La escalabilidad independiente y el desarrollo paralelo son ventajas, no desventajas.",
                600, 50, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("microservicios", "arquitectura", "facil"),
                disenoSistemas, facil, admin,
                option("Escalabilidad independiente", false, "Incorrecto: esa es una ventaja de los microservicios.", 1),
                option("Complejidad operativa", true, "Correcto: la complejidad de orquestación y monitoreo es una desventaja clave.", 2),
                option("Despliegue independiente", false, "Incorrecto: esa es una ventaja de los microservicios.", 3),
                option("Equipos autónomos", false, "Incorrecto: esa también es una ventaja.", 4));

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

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.MULTIPLE_CHOICE,
                "¿Cuáles de los siguientes son beneficios de usar colas de mensajes en sistemas distribuidos?",
                "Desacoplamiento, buffering y tolerancia a fallos.",
                "Las colas de mensajes desacoplan productores y consumidores, absorben picos de tráfico (buffering), y proporcionan tolerancia a fallos (los mensajes persisten). Reducen la latencia (procesamiento asíncrono), no la aumentan. El escalado horizontal es posible pero no automático.",
                800, 100, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("message-queue", "arquitectura", "intermedio"),
                disenoSistemas, intermedio, admin,
                option("Desacoplamiento de servicios", true, "Correcto: productores y consumidores no se conocen entre sí.", 1),
                option("Mayor latencia", false, "Incorrecto: las colas reducen la latencia percibida al procesar de forma asíncrona.", 2),
                option("Buffering de picos de tráfico", true, "Correcto: las colas absorven ráfagas de mensajes.", 3),
                option("Tolerancia a fallos", true, "Correcto: si un consumidor falla, los mensajes no se pierden.", 4));

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.SINGLE_CHOICE,
                "¿Qué sucede cuando un edge server de una CDN no tiene el contenido solicitado en caché?",
                "El edge server solicita el contenido al origin server.",
                "En un cache miss, el edge server actúa como proxy: solicita el contenido al servidor de origen, lo almacena localmente con un TTL configurable, y lo sirve al usuario. Las solicitudes siguientes para el mismo recurso serán cache hits hasta que expire el TTL.",
                900, 100, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("cdn", "cache", "distribucion"),
                disenoSistemas, intermedio, admin,
                option("Redirige al usuario al origin server", false, "Incorrecto: eso eliminaría el beneficio de la CDN.", 1),
                option("Responde con error 404", false, "Incorrecto: la CDN debe intentar obtener el contenido.", 2),
                option("Solicita el contenido al origin server", true, "Correcto: el edge hace un pull del origen y cachea el resultado.", 3),
                option("Replica el contenido desde otro edge", false, "Incorrecto: la replicación entre edges no es automática en CDN pull.", 4));

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

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.MULTIPLE_CHOICE,
                "¿Cuáles son ventajas del hashing consistente sobre el hashing tradicional?",
                "Minimiza la reorganización de datos al agregar o eliminar nodos.",
                "El hashing consistente solo requiere reasignar las claves del nodo adyacente cuando se agrega o elimina un nodo, a diferencia del hashing tradicional que requiere rehashear la mayoría de las claves. No elimina colisiones (solo las reduce) ni acelera el cálculo del hash.",
                1200, 150, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.5, \"designWeight\": 0.3}",
                List.of("consistent-hashing", "distribuidos", "avanzado"),
                disenoSistemas, avanzado, admin,
                option("Minimiza la reorganización al escalar", true, "Correcto: solo las claves del nodo vecino se reasignan.", 1),
                option("Elimina todas las colisiones", false, "Incorrecto: las colisiones pueden seguir ocurriendo.", 2),
                option("Facilita agregar o quitar nodos", true, "Correcto: agregar o quitar nodos afecta solo a los nodos adyacentes en el anillo.", 3),
                option("Es más rápido de calcular", false, "Incorrecto: el hashing consistente no es inherentemente más rápido.", 4));

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.SINGLE_CHOICE,
                "¿Qué nivel de consistencia se logra en DynamoDB cuando se configura con R + W > N (quorum)?",
                "Consistencia fuerte",
                "La fórmula R + W > N garantiza que hay superposición entre las réplicas leídas y escritas, asegurando que al menos una réplica tenga los datos más recientes. Por ejemplo, con N=3, R=2, W=2 se cumple 2+2 > 3 y se obtiene consistencia fuerte.",
                1200, 150, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.5, \"designWeight\": 0.3}",
                List.of("key-value-store", "consistencia", "avanzado"),
                disenoSistemas, avanzado, admin,
                option("Consistencia eventual", false, "Incorrecto: R+W <= N da consistencia eventual.", 1),
                option("Consistencia fuerte", true, "Correcto: R+W > N garantiza superposición de réplicas.", 2),
                option("Sin consistencia", false, "Incorrecto: siempre hay algún nivel de consistencia.", 3),
                option("Consistencia débil", false, "Incorrecto: R+W > N es el requisito para consistencia fuerte, no débil.", 4));

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
            Category category, DifficultyLevel difficulty, User createdBy,
            AnswerOption... options) {

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
        for (var opt : options) {
            opt.setQuestion(q);
            q.getAnswerOptions().add(opt);
        }
        questionRepo.save(q);
    }

    private AnswerOption option(String text, boolean correct, String explanation, int order) {
        var opt = new AnswerOption();
        opt.setOptionText(text);
        opt.setIsCorrect(correct);
        opt.setExplanation(explanation);
        opt.setDisplayOrder(order);
        return opt;
    }
}
