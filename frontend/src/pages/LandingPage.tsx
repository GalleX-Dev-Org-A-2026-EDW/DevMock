import { Code2, Trophy, TrendingUp, Target, ChevronRight, Play, CheckCircle2, Award, BarChart3, Users, Menu, X } from "lucide-react";
import { useState } from "react";
import { Link } from "react-router-dom";
import devMockIcon from "@/assets/DevMockIcono.png";

export default function LandingPage() {
  const [activeTab, setActiveTab] = useState<"students" | "professionals">("students");
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <div className="min-h-screen bg-background font-['Manrope']">
      {/* Navigation */}
      <nav className="fixed top-0 w-full bg-white/95 backdrop-blur-sm border-b border-border z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <img src={devMockIcon} alt="DevMock" className="h-8 sm:h-10 w-8 sm:w-10" />
            <span className="font-['Work_Sans'] font-bold text-lg sm:text-xl text-foreground">DevMock</span>
          </div>
          <div className="hidden md:flex items-center gap-8">
            <a href="#features" className="text-sm font-medium text-muted-foreground hover:text-foreground transition-colors">
              Características
            </a>
            <a href="#how-it-works" className="text-sm font-medium text-muted-foreground hover:text-foreground transition-colors">
              Cómo funciona
            </a>
            <a href="#pricing" className="text-sm font-medium text-muted-foreground hover:text-foreground transition-colors">
              Planes
            </a>
            <Link to="/login" className="text-sm font-medium text-foreground hover:text-primary/80 transition-colors">
              Iniciar sesión
            </Link>
            <Link to="/register" className="px-5 py-2.5 bg-primary text-primary-foreground rounded-lg text-sm font-semibold hover:bg-primary/90 transition-colors shadow-sm">
              Registrarse gratis
            </Link>
          </div>
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="md:hidden p-2 text-foreground hover:text-primary transition-colors"
          >
            {mobileMenuOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
          </button>
        </div>
        {mobileMenuOpen && (
          <div className="md:hidden bg-white border-t border-border px-4 sm:px-6 py-4 space-y-3">
            <a href="#features" onClick={() => setMobileMenuOpen(false)} className="block text-sm font-medium text-muted-foreground hover:text-foreground transition-colors">Características</a>
            <a href="#how-it-works" onClick={() => setMobileMenuOpen(false)} className="block text-sm font-medium text-muted-foreground hover:text-foreground transition-colors">Cómo funciona</a>
            <a href="#pricing" onClick={() => setMobileMenuOpen(false)} className="block text-sm font-medium text-muted-foreground hover:text-foreground transition-colors">Planes</a>
            <Link to="/login" onClick={() => setMobileMenuOpen(false)} className="block text-sm font-medium text-foreground hover:text-primary/80 transition-colors">Iniciar sesión</Link>
            <Link to="/register" onClick={() => setMobileMenuOpen(false)} className="block px-5 py-2.5 bg-primary text-primary-foreground rounded-lg text-sm font-semibold hover:bg-primary/90 transition-colors shadow-sm text-center">Registrarse gratis</Link>
          </div>
        )}
      </nav>

      {/* Hero Section */}
      <section className="relative pt-28 sm:pt-32 pb-16 sm:pb-20 px-4 sm:px-6 bg-gradient-to-br from-[#1a1a2e] via-[#16213e] to-[#0f3460] overflow-hidden">
        <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxnIGZpbGw9IiNmZmYiIGZpbGwtb3BhY2l0eT0iMC4wMyI+PGNpcmNsZSBjeD0iMzAiIGN5PSIzMCIgcj0iMiIvPjwvZz48L2c+PC9zdmc+')] opacity-50" />
        <div className="max-w-7xl mx-auto relative">
          <div className="grid lg:grid-cols-2 gap-16 items-center">
            <div className="text-white">
              <div className="inline-flex items-center gap-2 px-4 py-1.5 bg-white/10 backdrop-blur-sm rounded-full mb-8 border border-white/20">
                <span className="h-2 w-2 bg-emerald-400 rounded-full animate-pulse"></span>
                <span className="text-sm font-medium text-white/90">Entrenamiento profesional para entrevistas técnicas</span>
              </div>
              <h1 className="font-['Work_Sans'] font-extrabold text-4xl sm:text-5xl lg:text-6xl leading-tight mb-6 text-white">
                Domina tus<br />entrevistas técnicas
              </h1>
              <p className="text-base sm:text-lg text-white/70 mb-8 sm:mb-10 leading-relaxed max-w-lg">
                Practica con simulacros realistas, recibe retroalimentación multidimensional y compite en rankings globales.
                La plataforma definitiva para preparar tu próxima entrevista técnica.
              </p>
              <div className="flex flex-col sm:flex-row items-start sm:items-center gap-4">
                <Link to="/register" className="px-8 py-4 bg-emerald-500 text-white rounded-xl font-semibold hover:bg-emerald-600 transition-all shadow-lg hover:shadow-emerald-500/25 flex items-center gap-2 text-base">
                  Comenzar ahora
                  <ChevronRight className="h-5 w-5" />
                </Link>
                <button className="px-8 py-4 bg-white/10 backdrop-blur-sm text-white rounded-xl font-semibold hover:bg-white/20 transition-all border border-white/20 flex items-center gap-2 text-base">
                  <Play className="h-5 w-5" />
                  Ver demo
                </button>
              </div>
              <div className="mt-14 grid grid-cols-3 gap-8 border-t border-white/10 pt-10">
                <div>
                  <div className="font-['Work_Sans'] font-bold text-3xl text-emerald-400">12K+</div>
                  <div className="text-sm text-white/60 mt-1">Usuarios activos</div>
                </div>
                <div>
                  <div className="font-['Work_Sans'] font-bold text-3xl text-emerald-400">500+</div>
                  <div className="text-sm text-white/60 mt-1">Preguntas técnicas</div>
                </div>
                <div>
                  <div className="font-['Work_Sans'] font-bold text-3xl text-emerald-400">98%</div>
                  <div className="text-sm text-white/60 mt-1">Tasa de éxito</div>
                </div>
              </div>
            </div>
            <div className="relative">
              <div className="relative bg-white rounded-2xl shadow-2xl p-6 lg:p-8 border border-white/20">
                <div className="absolute -top-4 -left-4 w-24 h-24 bg-emerald-300/20 rounded-full blur-3xl"></div>
                <div className="absolute -bottom-4 -right-4 w-32 h-32 bg-blue-500/20 rounded-full blur-3xl"></div>
                <div className="relative">
                  <div className="flex items-center gap-3 mb-6 pb-4 border-b border-gray-100">
                    <div className="h-12 w-12 bg-blue-50 rounded-xl flex items-center justify-center">
                      <Code2 className="h-6 w-6 text-blue-600" />
                    </div>
                    <div>
                      <div className="font-semibold text-gray-900">Simulacro en progreso</div>
                      <div className="text-sm text-gray-500">Backend Engineering • Medium</div>
                    </div>
                  </div>
                  <div className="space-y-5">
                    <div className="flex items-start gap-3">
                      <CheckCircle2 className="h-5 w-5 text-emerald-500 mt-0.5 flex-shrink-0" />
                      <div className="flex-1">
                        <div className="text-sm font-medium text-gray-900 mb-2">¿Qué es un API RESTful?</div>
                        <div className="flex flex-wrap gap-2">
                          <span className="text-xs px-2.5 py-1 bg-emerald-50 text-emerald-700 rounded-md font-['JetBrains_Mono'] font-medium">Correctness: 95%</span>
                          <span className="text-xs px-2.5 py-1 bg-blue-50 text-blue-700 rounded-md font-['JetBrains_Mono'] font-medium">Clarity: 92%</span>
                        </div>
                      </div>
                    </div>
                    <div className="flex items-start gap-3">
                      <div className="h-5 w-5 border-2 border-blue-500 rounded-full mt-0.5 flex items-center justify-center flex-shrink-0">
                        <div className="h-2 w-2 bg-blue-500 rounded-full animate-pulse"></div>
                      </div>
                      <div className="flex-1">
                        <div className="text-sm font-medium text-gray-900 mb-2">Implementa un rate limiter</div>
                        <div className="bg-gray-50 rounded-xl p-4 font-['JetBrains_Mono'] text-xs leading-relaxed text-gray-900 border border-gray-100">
                          <span className="text-gray-400">class</span> RateLimiter {"{"}<br />
                          &nbsp;&nbsp;<span className="text-emerald-600">// Tu código aquí</span><br />
                          {"}"}
                        </div>
                      </div>
                    </div>
                    <div className="flex items-start gap-3 opacity-40">
                      <div className="h-5 w-5 border-2 border-gray-200 rounded-full mt-0.5 flex-shrink-0"></div>
                      <div className="flex-1">
                        <div className="text-sm font-medium text-gray-400">Pregunta 3/5</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-16 sm:py-24 px-4 sm:px-6 bg-gray-50">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <div className="inline-block px-4 py-1.5 bg-emerald-100 text-emerald-700 rounded-full text-sm font-semibold mb-4">
              Características
            </div>
            <h2 className="font-['Work_Sans'] font-bold text-3xl sm:text-4xl text-gray-900 mb-4">
              Todo lo que necesitas para prepararte
            </h2>
            <p className="text-base sm:text-lg text-gray-500 max-w-2xl mx-auto">
              Herramientas profesionales diseñadas para acelerar tu preparación y maximizar tus resultados
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {[
              {
                icon: Code2,
                title: "Banco de 500+ preguntas",
                description: "Preguntas teóricas y prácticas clasificadas por categoría, dificultad y tipo. Contenido actualizado semanalmente.",
                color: "text-blue-600 bg-blue-50"
              },
              {
                icon: Target,
                title: "Evaluación multidimensional",
                description: "Recibe feedback detallado en Correctness, Efficiency, Logic y Clarity con pesos configurables.",
                color: "text-emerald-600 bg-emerald-50"
              },
              {
                icon: Trophy,
                title: "Gamificación y logros",
                description: "Desbloquea achievements, acumula puntos y compite en rankings semanales, mensuales y históricos.",
                color: "text-amber-600 bg-amber-50"
              },
              {
                icon: TrendingUp,
                title: "Analíticas avanzadas",
                description: "Visualiza tu progreso, identifica fortalezas y debilidades por categoría y nivel de dificultad.",
                color: "text-cyan-600 bg-cyan-50"
              },
              {
                icon: BarChart3,
                title: "Rankings en tiempo real",
                description: "Compárate con otros desarrolladores en leaderboards globales filtrados por categoría y nivel.",
                color: "text-violet-600 bg-violet-50"
              },
              {
                icon: Award,
                title: "Certificados de logro",
                description: "Obtén certificados verificables al completar milestones y alcanzar puntuaciones destacadas.",
                color: "text-rose-600 bg-rose-50"
              }
            ].map((feature, idx) => (
              <div key={idx} className="bg-white rounded-xl p-6 border border-gray-100 hover:border-emerald-200 transition-all hover:shadow-lg hover:shadow-emerald-100/50 group">
                <div className={`inline-flex h-12 w-12 items-center justify-center rounded-lg ${feature.color} mb-4 group-hover:scale-110 transition-transform`}>
                  <feature.icon className="h-6 w-6" />
                </div>
                <h3 className="font-['Work_Sans'] font-semibold text-lg text-gray-900 mb-2">
                  {feature.title}
                </h3>
                <p className="text-sm text-gray-500 leading-relaxed">
                  {feature.description}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* How It Works */}
      <section id="how-it-works" className="py-16 sm:py-24 px-4 sm:px-6 bg-white">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <div className="inline-block px-4 py-1.5 bg-emerald-100 text-emerald-700 rounded-full text-sm font-semibold mb-4">
              Proceso
            </div>
            <h2 className="font-['Work_Sans'] font-bold text-3xl sm:text-4xl text-gray-900 mb-4">
              Cómo funciona DevMock
            </h2>
            <p className="text-base sm:text-lg text-gray-500 max-w-2xl mx-auto">
              Un flujo simple y efectivo para maximizar tu aprendizaje
            </p>
          </div>

          <div className="grid lg:grid-cols-4 gap-10">
            {[
              {
                step: "01",
                title: "Crea tu sesión",
                description: "Selecciona categoría, dificultad, número de preguntas y límite de tiempo según tu nivel."
              },
              {
                step: "02",
                title: "Responde preguntas",
                description: "Resuelve preguntas teóricas y prácticas en un ambiente que simula entrevistas reales."
              },
              {
                step: "03",
                title: "Recibe feedback",
                description: "Obtén evaluación detallada en cuatro dimensiones: corrección, eficiencia, lógica y claridad."
              },
              {
                step: "04",
                title: "Mejora y compite",
                description: "Analiza tus métricas, identifica áreas de mejora y sube en los rankings globales."
              }
            ].map((item, idx) => (
              <div key={idx} className="relative">
                {idx < 3 && (
                  <div className="hidden lg:block absolute top-14 left-[60%] w-[80%] h-0.5 bg-gradient-to-r from-emerald-200 to-transparent"></div>
                )}
                <div className="relative text-center lg:text-left">
                  <div className="font-['JetBrains_Mono'] font-bold text-6xl text-emerald-100 mb-4 leading-none">
                    {item.step}
                  </div>
                  <h3 className="font-['Work_Sans'] font-semibold text-xl text-gray-900 mb-3">
                    {item.title}
                  </h3>
                  <p className="text-sm text-gray-500 leading-relaxed">
                    {item.description}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Pricing / Plans */}
      <section id="pricing" className="py-16 sm:py-24 px-4 sm:px-6 bg-gray-50">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-12">
            <div className="inline-block px-4 py-1.5 bg-emerald-100 text-emerald-700 rounded-full text-sm font-semibold mb-4">
              Planes
            </div>
            <h2 className="font-['Work_Sans'] font-bold text-3xl sm:text-4xl text-gray-900 mb-4">
              Elige tu nivel de preparación
            </h2>
          </div>

          <div className="flex justify-center mb-12">
            <div className="inline-flex bg-gray-200/70 rounded-xl p-1.5 flex-wrap justify-center">
              <button
                onClick={() => setActiveTab("students")}
                className={`px-8 py-2.5 rounded-lg text-sm font-semibold transition-all ${
                  activeTab === "students"
                    ? "bg-white text-gray-900 shadow-sm"
                    : "text-gray-500 hover:text-gray-900"
                }`}
              >
                Estudiantes
              </button>
              <button
                onClick={() => setActiveTab("professionals")}
                className={`px-8 py-2.5 rounded-lg text-sm font-semibold transition-all ${
                  activeTab === "professionals"
                    ? "bg-white text-gray-900 shadow-sm"
                    : "text-gray-500 hover:text-gray-900"
                }`}
              >
                Profesionales
              </button>
            </div>
          </div>

          <div className="grid md:grid-cols-2 gap-8 max-w-4xl mx-auto">
            <div className="bg-white rounded-2xl p-8 border border-gray-100 shadow-sm">
              <div className="mb-6">
                <h3 className="font-['Work_Sans'] font-bold text-2xl text-gray-900 mb-2">Gratis</h3>
                <div className="flex items-baseline gap-1 mb-4">
                  <span className="font-['Work_Sans'] font-bold text-4xl text-gray-900">$0</span>
                  <span className="text-gray-400">/mes</span>
                </div>
                <p className="text-sm text-gray-500">
                  Perfecto para comenzar tu preparación
                </p>
              </div>
              <ul className="space-y-3 mb-8">
                {[
                  "50 preguntas disponibles",
                  "Evaluación básica",
                  "Rankings semanales",
                  "3 simulacros por semana",
                  "Estadísticas básicas"
                ].map((item, idx) => (
                  <li key={idx} className="flex items-center gap-3 text-sm">
                    <CheckCircle2 className="h-5 w-5 text-emerald-500 flex-shrink-0" />
                    <span className="text-gray-700">{item}</span>
                  </li>
                ))}
              </ul>
              <Link to="/register" className="block w-full px-6 py-3.5 bg-white text-gray-900 border-2 border-gray-200 rounded-xl font-semibold hover:bg-gray-50 hover:border-gray-300 transition-all text-center">
                Comenzar gratis
              </Link>
            </div>

            <div className="bg-gradient-to-br from-[#1a1a2e] via-[#16213e] to-[#0f3460] rounded-2xl p-8 border border-gray-200/20 relative overflow-hidden shadow-xl">
              <div className="absolute top-5 right-5 px-3 py-1 bg-emerald-500 text-white rounded-full text-xs font-semibold shadow-lg">
                Más popular
              </div>
              <div className="relative z-10 text-white">
                <div className="mb-6">
                  <h3 className="font-['Work_Sans'] font-bold text-2xl mb-2">
                    {activeTab === "students" ? "Estudiante Pro" : "Professional"}
                  </h3>
                  <div className="flex items-baseline gap-1 mb-4">
                    <span className="font-['Work_Sans'] font-bold text-4xl">
                      ${activeTab === "students" ? "9" : "19"}
                    </span>
                    <span className="text-white/60">/mes</span>
                  </div>
                  <p className="text-sm text-white/70">
                    {activeTab === "students"
                      ? "Para estudiantes que buscan destacar"
                      : "Para profesionales exigentes"}
                  </p>
                </div>
                <ul className="space-y-3 mb-8">
                  {(activeTab === "students" ? [
                    "500+ preguntas completas",
                    "Evaluación multidimensional",
                    "Rankings globales ilimitados",
                    "Simulacros ilimitados",
                    "Analíticas avanzadas",
                    "Achievements y certificados"
                  ] : [
                    "Todo de Estudiante Pro",
                    "Métricas avanzadas de rendimiento",
                    "Comparación con top performers",
                    "Preguntas de nivel senior",
                    "Simulacros de empresas FAANG",
                    "Soporte prioritario"
                  ]).map((item, idx) => (
                    <li key={idx} className="flex items-center gap-3 text-sm">
                      <CheckCircle2 className="h-5 w-5 text-emerald-400 flex-shrink-0" />
                      <span className="text-white/90">{item}</span>
                    </li>
                  ))}
                </ul>
                <button className="w-full px-6 py-3.5 bg-white text-[#1a1a2e] rounded-xl font-semibold hover:bg-gray-100 transition-all shadow-lg">
                  Obtener {activeTab === "students" ? "Estudiante Pro" : "Professional"}
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Final */}
      <section className="relative py-16 sm:py-24 px-4 sm:px-6 bg-gradient-to-br from-[#1a1a2e] via-[#16213e] to-[#0f3460] overflow-hidden">
        <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxnIGZpbGw9IiNmZmYiIGZpbGwtb3BhY2l0eT0iMC4wMyI+PGNpcmNsZSBjeD0iMzAiIGN5PSIzMCIgcj0iMiIvPjwvZz48L2c+PC9zdmc+')] opacity-50" />
        <div className="max-w-4xl mx-auto text-center relative">
          <h2 className="font-['Work_Sans'] font-extrabold text-3xl sm:text-4xl lg:text-5xl text-white mb-6">
            Comienza a prepararte hoy
          </h2>
          <p className="text-base sm:text-lg text-white/70 mb-8 sm:mb-10 max-w-2xl mx-auto">
            Únete a miles de desarrolladores que están mejorando sus habilidades y consiguiendo mejores oportunidades
          </p>
          <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
            <Link to="/register" className="px-8 py-4 bg-emerald-500 text-white rounded-xl font-semibold hover:bg-emerald-600 transition-all shadow-lg hover:shadow-emerald-500/25 flex items-center gap-2 text-lg">
              Crear cuenta gratis
              <ChevronRight className="h-5 w-5" />
            </Link>
            <button className="px-8 py-4 bg-white/10 backdrop-blur-sm text-white rounded-xl font-semibold hover:bg-white/20 transition-all border border-white/20 flex items-center gap-2 text-lg">
              <Users className="h-5 w-5" />
              Hablar con ventas
            </button>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="py-12 sm:py-16 px-4 sm:px-6 bg-[#0d1117] text-white">
        <div className="max-w-7xl mx-auto">
          <div className="grid md:grid-cols-4 gap-10 mb-10">
            <div>
              <div className="flex items-center gap-2 mb-4">
                <img src={devMockIcon} alt="DevMock" className="h-8 w-8 brightness-0 invert" />
                <span className="font-['Work_Sans'] font-bold text-lg">DevMock</span>
              </div>
              <p className="text-sm text-gray-400 leading-relaxed">
                Plataforma profesional de entrenamiento para entrevistas técnicas.
              </p>
            </div>
            <div>
              <h4 className="font-['Work_Sans'] font-semibold mb-4 text-gray-200">Producto</h4>
              <ul className="space-y-2.5 text-sm text-gray-400">
                <li><a href="#" className="hover:text-white transition-colors">Características</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Precios</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Testimonios</a></li>
              </ul>
            </div>
            <div>
              <h4 className="font-['Work_Sans'] font-semibold mb-4 text-gray-200">Empresa</h4>
              <ul className="space-y-2.5 text-sm text-gray-400">
                <li><a href="#" className="hover:text-white transition-colors">Sobre nosotros</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Blog</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Contacto</a></li>
              </ul>
            </div>
            <div>
              <h4 className="font-['Work_Sans'] font-semibold mb-4 text-gray-200">Legal</h4>
              <ul className="space-y-2.5 text-sm text-gray-400">
                <li><a href="#" className="hover:text-white transition-colors">Privacidad</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Términos</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Cookies</a></li>
              </ul>
            </div>
          </div>
          <div className="pt-8 border-t border-gray-800 text-center text-sm text-gray-500">
            © 2026 DevMock. Todos los derechos reservados.
          </div>
        </div>
      </footer>
    </div>
  );
}
