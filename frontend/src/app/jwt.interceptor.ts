import { ToastrService } from 'ngx-toastr';
import { Injectable } from '@angular/core';
import { AuthService } from './services/auth/auth.service';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import {tap} from 'rxjs/internal/operators';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(public auth: AuthService, private router: Router, private toastr: ToastrService) {}
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    return next.handle(request).pipe(tap((event: HttpEvent<any>) => {
        if (event instanceof HttpResponse) {
            // alert('GOOD RESPONSE');
        }
    }, (err: any) => {
        // alert('BAD RESPONSE');
        if (err instanceof HttpErrorResponse) {

            console.error(err.message);

            switch (err.status) {
                case 0: {
                    this.toastr.error('Server unavaliable');
                    break;
                }
                case 401: {
                    this.toastr.error('Unauthorized');
                    this.auth.collectFailedRequest(request);
                    this.router.navigate(['/login']);
                    break;
                }
                default: {
                    this.toastr.error(err.error);
                    break;
                }
            }
        }
    }));
  }
}
