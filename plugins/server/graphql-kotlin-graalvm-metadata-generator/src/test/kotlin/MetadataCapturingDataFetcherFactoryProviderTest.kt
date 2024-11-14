import com.expediagroup.graphql.plugin.graalvm.MetadataCapturingDataFetcherFactoryProvider
import com.expediagroup.graphql.plugin.graalvm.enums.EnumQuery
import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.reflect.KFunction

class MetadataCapturingDataFetcherFactoryProviderTest {

    private lateinit var scanResult: ScanResult
    private lateinit var provider: MetadataCapturingDataFetcherFactoryProvider

    @BeforeEach
    fun setup() {
        scanResult = ClassGraph().enableAllInfo().acceptPackages("com.expediagroup.graphql.plugin.graalvm").scan()
        provider = MetadataCapturingDataFetcherFactoryProvider(scanResult, listOf("com.expediagroup.graphql.plugin.graalvm"))

        val kClass = EnumQuery::class
        val kFunction = kClass.members.find { it.name == "enumArgQuery" } as KFunction<*>
        provider.functionDataFetcherFactory(null, kClass, kFunction)
    }

    @Test
    fun `reflectMetadata should not be empty`() {
        val metadata = provider.reflectMetadata()

        Assertions.assertNotNull(metadata)
        Assertions.assertTrue(metadata.isNotEmpty(), "Reflect metadata should not be empty")
        Assertions.assertEquals(listOf("com.expediagroup.graphql.plugin.graalvm"), provider.supportedPackages)
    }
}
