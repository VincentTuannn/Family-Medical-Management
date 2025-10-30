import { Routes } from '@angular/router';
import { PatientContainer } from './user/patient/container/container';
import { DashboardContainer } from './user/dashboard/container/container';
import { DoctorContainer } from './user/doctor/container/container';



export const routes: Routes = [
    { path: '', component: DashboardContainer },
    { path: 'patients', component: PatientContainer },
    { path: 'doctor', component: DoctorContainer }
    // { path: '**', redirectTo: '/dashboard' }
];
