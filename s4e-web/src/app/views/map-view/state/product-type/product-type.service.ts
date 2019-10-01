import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ProductTypeStore} from './product-type.store';
import {ProductType} from './product-type.model';
import {finalize} from 'rxjs/operators';
import {ProductService} from '../product/product.service';
import {ProductTypeQuery} from './product-type.query';
import {S4eConfig} from '../../../../utils/initializer/config.service';
import {ProductStore} from '../product/product.store';

@Injectable({providedIn: 'root'})
export class ProductTypeService {

  constructor(private productTypeStore: ProductTypeStore,
              private productStore: ProductStore,
              private CONFIG: S4eConfig,
              private http: HttpClient,
              private productTypeQuery: ProductTypeQuery,
              private productService: ProductService) {
  }

  get() {
    this.productStore.setLoading(true);
    this.http.get<ProductType[]>(`${this.CONFIG.apiPrefixV1}/productTypes`).pipe(
      finalize(() => this.productStore.setLoading(false)),
    ).subscribe(data => this.productTypeStore.set(data));
  }

  setActive(productTypeId: number | null) {
    if (productTypeId != null && this.productTypeQuery.getActiveId() !== productTypeId) {
      if (this.productTypeQuery.getEntity(productTypeId).productIds === undefined) {
        this.productService.get(productTypeId);
      }
      this.productTypeStore.setActive(productTypeId);
    } else {
      this.productTypeStore.setActive(null);
      this.productStore.setActive(null);
    }
  }
}
