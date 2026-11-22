import { HashLocationStrategy, LocationStrategy, registerLocaleData } from '@angular/common';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import localeFr from '@angular/common/locales/fr';
import { ApplicationConfig, LOCALE_ID, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter, withComponentInputBinding, withInMemoryScrolling } from '@angular/router';
import { routes } from './app.routes';
import { apiInterceptor } from './interceptors/api.interceptor';
import { chargementInterceptor } from './interceptors/chargement.interceptor';

registerLocaleData(localeFr);

export const appConfig: ApplicationConfig = {
	providers: [
		provideBrowserGlobalErrorListeners(),
		provideRouter(routes, withComponentInputBinding(), withInMemoryScrolling({ scrollPositionRestoration: 'top' })),
		provideHttpClient(withInterceptors([chargementInterceptor, apiInterceptor])),
		{ provide: LocationStrategy, useClass: HashLocationStrategy },
		{ provide: LOCALE_ID, useValue: 'fr' },
	],
};
