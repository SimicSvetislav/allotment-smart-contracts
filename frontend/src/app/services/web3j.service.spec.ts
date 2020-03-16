import { TestBed } from '@angular/core/testing';

import { Web3jService } from './web3j.service';

describe('Web3jService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: Web3jService = TestBed.get(Web3jService);
    expect(service).toBeTruthy();
  });
});
