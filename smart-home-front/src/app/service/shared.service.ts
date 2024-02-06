import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SharedService {
  constructor() {}

  private isSignInSource = new BehaviorSubject<boolean>(false);
  isSignIn$ = this.isSignInSource.asObservable();

  setIsSignIn(value: boolean): void {
    this.isSignInSource.next(value);
  }
}
