import {Provider} from '@angular/core';
import {S4eConfig} from './utils/initializer/config.service';

export const TestingConfigProvider: Provider = {
  provide: S4eConfig, useValue: {
    geoserverUrl: 'http://localhost:8080/geoserver/wms',
    geoserverWorkspace: 'testing',
    backendDateFormat: 'yyyy-MM-dd\'T\'HH:mm:ss',
    recaptchaSiteKey: 'abc123',
    projection: {toProjection: 'EPSG:3857', coordinates: [19, 52]},
    apiPrefixV1: 'api/v1',
    userLocalStorageKey: 'user',
    generalErrorKey: '__general__'
  }
};
