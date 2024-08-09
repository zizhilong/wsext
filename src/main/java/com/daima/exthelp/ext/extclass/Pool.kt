import com.daima.exthelp.ext.extclass.ClassInterface
import com.daima.exthelp.ext.extclass.ExtBaseClass
import com.daima.exthelp.ext.extclass.ExtClass
import com.daima.exthelp.ext.extclass.parseFile
import com.daima.exthelp.ext.ns.Class2Psi
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

// 定义一个存储 ClassInterface 实例的单例池类
object Pool {
    // 使用 MutableList 存储接口实现对象
    private val classInstances: MutableList<ClassInterface<*>> = mutableListOf()

    // ClassFactory 用于在找不到对象时创建对象

    // 添加对象到池中
    fun <T> addInstance(instance: ClassInterface<T>) {
        classInstances.add(instance)
    }

    // 移除对象
    fun <T> removeInstance(instance: ClassInterface<T>) {
        classInstances.remove(instance)
    }

    // 获取池中所有对象
    fun <T> getAllInstances(): List<ClassInterface<T>> {
        return classInstances.filterIsInstance<ClassInterface<T>>()
    }

    // 根据类名查找对象，找不到时尝试创建
    fun <T> findByClassName(project: Project, className: String): ClassInterface<T>? {
        val instance = classInstances.find { it.getClassName() == className }
        if (instance != null) {
            @Suppress("UNCHECKED_CAST")
            return instance as ClassInterface<T>
        }

        // 如果 className 以 "Ext" 开头，调用 PoolLoadExtBase
        if (className.startsWith("Ext")) {
            val extInstance = PoolLoadExtBase<T>(className)
            if (extInstance != null) {
                addInstance(extInstance)
                return extInstance
            }
        } else {
            // 否则，正常使用 Class2Psi 和 parseFile 创建实例
            val psiFile: PsiFile? = Class2Psi(className, project)
            if (psiFile != null) {
                val ret = parseFile(psiFile)
                if (ret != null) {
                    addInstance(ret)
                    return ret as ClassInterface<T>
                }
            }
        }
        return null
    }

    // 如果类名以 "Ext" 开头，使用这个函数加载并创建实例
    private fun <T> PoolLoadExtBase( className: String): ClassInterface<T>? {
        // 这里可以定义如何创建 Ext 开头的类名的实例
        // 例如，假设我们创建一个示例 ExtClass
        // 创建 ExtClass 对象
        val extClass = ExtBaseClass(className);
        return extClass as? ClassInterface<T>


    }
}