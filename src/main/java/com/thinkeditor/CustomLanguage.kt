import com.intellij.lang.Language

object CustomLanguage  : Language("TXML") {
    private fun readResolve(): Any = CustomLanguage
}