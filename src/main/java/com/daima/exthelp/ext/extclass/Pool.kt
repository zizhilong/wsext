import com.daima.exthelp.ext.extclass.ClassInterface
import com.daima.exthelp.ext.ns.Class2Psi
import com.intellij.openapi.project.Project

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
        // 如果找不到，尝试使用工厂创建实例
        @Suppress("UNCHECKED_CAST")
        //获得psi对象
        var psi=Class2Psi(className,project)


        return null
    }
}