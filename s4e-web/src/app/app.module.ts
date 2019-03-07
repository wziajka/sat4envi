import { NgModule } from '@angular/core';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {HTTP_INTERCEPTORS, HttpClient} from '@angular/common/http';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import { RouterModule } from '@angular/router';
import {BrowserModule} from '@angular/platform-browser';

import {CommonModule} from './common.module';
import {MapModule} from './map/map.module';
import {ProfileModule} from './profile/profile.module';
import {AppComponent} from './app.component';
import {ContentTypeInterceptor} from './utils/content-type.interceptor';
import {AuthInterceptor} from './utils/auth.interceptor';
import {ErrorInterceptor} from './utils/error.interceptor';
import { routes } from './routes';
import {MatCommonModule} from '@angular/material/core';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatExpansionModule} from '@angular/material';

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http);
}

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forRoot(routes),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient],
      },

    }),

    MapModule,
    MatCommonModule,
    ProfileModule,
    BrowserModule,
    BrowserAnimationsModule,
    MatExpansionModule,

  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: ContentTypeInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
  ],
  bootstrap: [AppComponent],
})
export class AppModule { }
