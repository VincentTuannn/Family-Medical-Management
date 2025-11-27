import { Routes } from '@angular/router';
import { PatientContainer } from './user/patient/container/container';
import { DashboardContainer } from './user/dashboard/container/container';
import { DoctorContainer } from './user/doctor/container/container';
import { LoginComponent } from './user/login/login';
import { LoginAdminComponent } from './admin/login-admin/login-admin';
import { authGuard } from './features/service/auth-service/auth.guard';
import { RegisterComponent } from './user/register/register';
import { DashboardAdminComponent } from './admin/dashboard-admin/dashboard-admin';
import { PatientAdminContainer } from './admin/patient-admin/container/container';
import { DoctorAdminContainer } from './admin/doctor-admin/container/container';
import { UserAdminContainer } from './admin/user-admin/container/container';
import { AppointmentContainer } from './user/appointment/container/container';
import { ChatContainer } from './user/chat/container/container';



export const routes: Routes = [
    { path: '', redirectTo: '/login', pathMatch: 'full' },
    { path: 'login', component: LoginComponent },
    { path: 'admin', component: LoginAdminComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'dashboard', component: DashboardContainer, canActivate: [authGuard] },
    { path: 'dashboard-admin', component: DashboardAdminComponent, canActivate: [authGuard] },
    { path: 'patients', component: PatientContainer, canActivate: [authGuard] },
    { path: 'doctor', component: DoctorContainer, canActivate: [authGuard] },
    { path: 'appointments', component: AppointmentContainer, canActivate: [authGuard] },
    { path: 'chat', component: ChatContainer, canActivate: [authGuard] },
    // Admin routes
    { path: 'admin/patients', component: PatientAdminContainer, canActivate: [authGuard] },
    { path: 'admin/doctors', component: DoctorAdminContainer, canActivate: [authGuard] },
    { path: 'admin/users', component: UserAdminContainer, canActivate: [authGuard] },
    { path: '**', redirectTo: '/login' }
];
