import {NgModule} from '@angular/core';
import {CommonModule} from '../common.module';

import {MapComponent} from './map.component';
import {ViewManagerComponent} from './view-manager.component';
import {LoginModule} from '../login/login.module';
import {MatExpansionModule} from '@angular/material';

@NgModule({
  declarations: [
    MapComponent,
    ViewManagerComponent,
  ],
  exports: [
    MapComponent,
  ],
  imports: [
    CommonModule,
    LoginModule,
    MatExpansionModule,
  ],
  providers: [],
})
export class MapModule { }
