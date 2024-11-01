import { inject } from '@angular/core';
import { Router } from '@angular/router';
import {
  CanActivateFn,
  ActivatedRouteSnapshot,
  RouterStateSnapshot
} from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';

export const AuthGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  if (isPlatformBrowser(platformId)) {
    const userEmail = localStorage.getItem('user-email');

    if (userEmail) {
      return true;
    }
  }

  router.navigate(['/login']);
  return false;
};
