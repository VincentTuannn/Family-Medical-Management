import { Routes } from '@angular/router';
import { PatientContainer } from './user/patient/container/container';


export const routes: Routes = [
    { path: 'patients', component: PatientContainer },
    { path: '**', redirectTo: '/dashboard' }
];
