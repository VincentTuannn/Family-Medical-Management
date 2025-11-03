import { Routes } from '@angular/router';
import { PatientContainer } from './user/patient/container/container';
import { DashboardContainer } from './user/dashboard/container/container';
import { DoctorContainer } from './user/doctor/container/container';
import { LoginComponent } from './user/login/login';
import { authGuard } from './features/service/auth-service/auth.guard';
import { RegisterComponent } from './user/register/register';



export const routes: Routes = [
    { path: '', redirectTo: '/login', pathMatch: 'full' },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'dashboard', component: DashboardContainer}, //canActivate: [authGuard] },
    { path: 'patients', component: PatientContainer}, //canActivate: [authGuard] },
    { path: 'doctor', component: DoctorContainer}, //canActivate: [authGuard] },
    { path: '**', redirectTo: '/login' }
];
