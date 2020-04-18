import { Representative } from './../Representative';
import { Period } from './../Period';
import { Reservation, SingleReservation } from './../Reservation';
import { Contract } from './../Contract';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { WEB3J_API } from './../globals';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Web3jService {
  constructor(private http: HttpClient) { }

  public test(): Observable<string> {
    return this.http.get(WEB3J_API + 'test', {responseType: 'text'});
  }

  public proposeContract(contract: Contract): Observable<any> {
    return this.http.post(WEB3J_API + 'deployA', contract, {responseType: 'text'})
  }

  public getContractInfo(id: number): Observable<any> {
    return this.http.get(WEB3J_API + 'contract/' + id);
  }

  getContractsByOrgAndStatus(orgId: number, status: string): Observable<any> {
    return this.http.get(WEB3J_API + 'contracts/' + orgId + '/' + status);
  }

  accept(id: number, repr: number): Observable<string> {
    return this.http.get(WEB3J_API + 'accept/' + id + '/' + repr, {responseType: 'text'});
  }
  reject(id: number) {
    return this.http.get(WEB3J_API + 'reject/' + id, {responseType: 'text'});
  }

  reserve(id: number, reprId:number,  reservation: SingleReservation): Observable<any> {
    return this.http.post(WEB3J_API + 'reservation/' + id + '/' + reprId, reservation);
  }

  withdraw(id: number, reprId: number, period: Period): Observable<any> {
    return this.http.post(WEB3J_API + 'withdrawal/' + id + '/' + reprId, period);
  }

  break(id: number, repr: number,  reprType: string): Observable<any> {

    if (reprType === 'Agency') {
      return this.http.get(WEB3J_API + 'breakAg/' + id + '/' + repr);
    } else {
      return this.http.get(WEB3J_API + 'breakAcc/' + id + '/' + repr);
    }
  }

  getReservations(contractId: number): Observable<any> {
    return this.http.get(WEB3J_API + 'reservations/' + contractId);
  }

  verifyReservation(contractId: number, reservationId: number, beds: number, representativeId: number): Observable<any> {
    return this.http.get(WEB3J_API + 'verify/' + contractId + '/' + reservationId + '/' + beds + '/' + representativeId, {responseType: 'text'});
  }

}
