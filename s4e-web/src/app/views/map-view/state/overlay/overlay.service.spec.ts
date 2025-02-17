import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {OverlayService} from './overlay.service';
import {OverlayStore} from './overlay.store';
import {TestingConfigProvider} from '../../../../app.configuration.spec';

describe('OverlayService', () => {
  let overlayService: OverlayService;
  let overlayStore: OverlayStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [OverlayService, OverlayStore, TestingConfigProvider],
      imports: [HttpClientTestingModule]
    });

    overlayService = TestBed.get(OverlayService);
    overlayStore = TestBed.get(OverlayStore);
  });

  it('should be created', () => {
    expect(overlayService).toBeDefined();
  });

});
