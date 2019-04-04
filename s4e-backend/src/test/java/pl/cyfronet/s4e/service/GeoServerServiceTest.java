package pl.cyfronet.s4e.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import pl.cyfronet.s4e.BasicTest;
import pl.cyfronet.s4e.bean.Product;
import pl.cyfronet.s4e.bean.ProductType;
import pl.cyfronet.s4e.data.repository.ProductRepository;
import pl.cyfronet.s4e.data.repository.ProductTypeRepository;
import pl.cyfronet.s4e.util.S3AddressUtil;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@BasicTest
class GeoServerServiceTest {

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private S3AddressUtil s3AddressUtil;

    @Mock
    private GeoServerOperations geoServerOperations;

    @Value("${geoserver.workspace}")
    private String workspace;

    private GeoServerService geoServerService;
    private ProductType productType;
    private Product product;

    @BeforeEach
    private void beforeEach() {
        productRepository.deleteAll();
        productTypeRepository.deleteAll();
    }

    private void prepare() {
        geoServerService = new GeoServerService(geoServerOperations, productService, s3AddressUtil);
        ReflectionTestUtils.setField(geoServerService, "workspace", workspace);

        productType = productTypeRepository.save(
                ProductType.builder()
                        .name("productType")
                        .build());
        product = productRepository.save(
                Product.builder()
                        .productType(productType)
                        .timestamp(LocalDateTime.now())
                        .layerName("testLayerName")
                        .s3Path("some/s3/path.tif")
                        .build());
    }

    @Test
    public void shouldAddLayerAndSetCreatedFlag() {
        prepare();

        assertThat(product.isLayerCreated(), is(equalTo(false)));

        geoServerService.addLayer(product);

        Product updatedProduct = productRepository.findById(product.getId()).get();
        assertThat(updatedProduct.isLayerCreated(), is(equalTo(true)));
        verify(geoServerOperations, times(1)).createS3CoverageStore(workspace, product.getLayerName(), s3AddressUtil.getS3Address(product.getS3Path()));
        verify(geoServerOperations, times(1)).createS3Coverage(workspace, product.getLayerName(), product.getLayerName());
        verifyNoMoreInteractions(geoServerOperations);
    }

    @Test
    public void shouldTryToRollbackWhenAddLayerThrows() {
        prepare();
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(geoServerOperations).createS3CoverageStore(any(), any(), any());

        assertThat(product.isLayerCreated(), is(equalTo(false)));

        assertThrows(HttpClientErrorException.class, () -> geoServerService.addLayer(product));

        Product updatedProduct = productRepository.findById(product.getId()).get();
        assertThat(updatedProduct.isLayerCreated(), is(equalTo(false)));
        verify(geoServerOperations, times(1)).createS3CoverageStore(workspace, product.getLayerName(), s3AddressUtil.getS3Address(product.getS3Path()));
        verify(geoServerOperations, times(1)).deleteCoverageStore(workspace, product.getLayerName(), true);
        verifyNoMoreInteractions(geoServerOperations);
    }
}
