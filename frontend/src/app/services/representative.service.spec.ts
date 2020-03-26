import { TestBed } from '@angular/core/testing';

import { RepresentativeService } from './representative.service';

describe('RepresentativeService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: RepresentativeService = TestBed.get(RepresentativeService);
    expect(service).toBeTruthy();
  });
});
