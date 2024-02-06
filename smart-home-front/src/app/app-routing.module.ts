import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PropertiesPageComponent } from './components/properties-page/properties-page.component';
import { AddPropertyPageComponent } from './components/add-property-page/add-property-page.component';
import { AuthPageComponent } from './components/auth-page/auth-page.component';
import { CreateAdminComponent } from './components/create-admin/create-admin.component';
import { UserPagesGuard } from './guards/user-pages.guard';
import { AuthorizedGuard } from './guards/authorized.guard';
import { DevicesPageComponent } from './components/devices-page/devices-page.component';
import { AddDevicePageComponent } from './components/add-device-page/add-device-page.component';
import { SuperAdminPagesGuard } from './guards/super-admin-pages.guard';
import { ResetPassowrdComponent } from './components/reset-passowrd/reset-passowrd.component';
import { AmbientSensorComponent } from './components/pka-devices/ambient-sensor/ambient-sensor-history-component/ambient-sensor-history.component';
import {AirConditionHistoryComponent} from './components/pka-devices/air-condition/air-condition-history/air-condition-history.component'
import { LampInfoPageComponent } from './components/spu-devices/lamp/lamp-info-page/lamp-info-page.component';
import { AirConditionerManagerComponent } from './components/pka-devices/air-condition/air-conditioner-manager/air-conditioner-manager.component';
import {PowerConsumptionComponent} from "./components/power-consumption/power-consumption.component";
import {
  SolarPanelSystemDetailsComponent
} from "./components/solar-panel-system-details/solar-panel-system-details.component";
import { GateInfoPageComponent } from './components/spu-devices/gate/gate-info-page/gate-info-page.component';
import {
  SprinklerSystemInfoPageComponent
} from "./components/spu-devices/sprinkler-system/sprinkler-system-info-page/sprinkler-system-info-page.component";
import {
  ElectricVehicleChargerDetailsComponent
} from "./components/electric-vehicle-charger-details/electric-vehicle-charger-details.component";
import { WashingMachineHistoryComponent } from './components/pka-devices/washing-machine/washing-machine-history/washing-machine-history.component';
import { WashingMachineManagerComponent } from './components/pka-devices/washing-machine/washing-machine-manager/washing-machine-manager.component';
import { ObtainedPermissionDevicesPageComponent } from './components/permission/obtained-permission-devices-page/obtained-permission-devices-page.component';
import { PermissionManagmentPageComponent as PermissionManagementPageComponent } from './components/permission/permission-managment-page/permission-managment-page.component';
import {HomeBatteryInfoPageComponent} from "./components/home-battery-info-page/home-battery-info-page.component";
import {AdminPowerConsumptionComponent} from "./components/admin-power-consumption/admin-power-consumption.component";

const routes: Routes = [
  { path: '', component: AuthPageComponent },
  {
    path: 'properties',
    component: PropertiesPageComponent,
    canActivate: [AuthorizedGuard],
  },
  {
    path: 'add-property',
    component: AddPropertyPageComponent,
    canActivate: [UserPagesGuard],
  },
  { path: 'devices/:propertyId', component: DevicesPageComponent },
  { path: 'add-device/:propertyId', component: AddDevicePageComponent },
  { path: '', component: AuthPageComponent },
  {
    path: 'properties',
    component: PropertiesPageComponent,
    canActivate: [AuthorizedGuard],
  },
  {
    path: 'add-property',
    component: AddPropertyPageComponent,
    canActivate: [UserPagesGuard],
  },
  {
    path: 'add-admin',
    component: CreateAdminComponent,
    canActivate: [SuperAdminPagesGuard],
  },
  { path: 'reset-password', component: ResetPassowrdComponent },
  { path: 'graph-ambient-sensor', component: AmbientSensorComponent },
  { path: 'air-condition-history', component: AirConditionHistoryComponent}, // TODO remove?
  { path: 'lamp-info-page/:lampId', component:LampInfoPageComponent },
  { path: 'gate-info-page/:gateId', component: GateInfoPageComponent },
  { path: 'ambient-sensor-history/:deviceId', component: AmbientSensorComponent },
  { path: 'air-conditioner-history/:deviceId', component: AirConditionHistoryComponent},
  { path: 'air-conditioner-manager/:deviceId', component: AirConditionerManagerComponent},
  { path: 'power-consumption/:propertyId', component: PowerConsumptionComponent},
  { path: 'solar-panel/:deviceId', component: SolarPanelSystemDetailsComponent},
  { path: 'sprinkler-system-info-page/:sprinklerSystemId', component: SprinklerSystemInfoPageComponent},
  { path: 'electric-vehicle-charger-details/:deviceId', component: ElectricVehicleChargerDetailsComponent },
  { path: 'washing-machine-manager/:deviceId', component: WashingMachineManagerComponent},
  { path: 'washing-machine-history/:deviceId', component: WashingMachineHistoryComponent},
  { path: 'permission-devices/:propertyId', component: ObtainedPermissionDevicesPageComponent },
  { path: 'permission-management', component: PermissionManagementPageComponent },
  { path: 'home-battery-info-page/:deviceId', component: HomeBatteryInfoPageComponent},
  { path: 'admin-power-consumption', component: AdminPowerConsumptionComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
