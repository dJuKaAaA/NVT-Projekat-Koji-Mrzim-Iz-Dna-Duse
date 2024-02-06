import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { PropertiesPageComponent } from './components/properties-page/properties-page.component';
import { PropertyCardComponent } from './components/property-card/property-card.component';
import { AddPropertyPageComponent } from './components/add-property-page/add-property-page.component';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NavbarComponent } from './components/navbar/navbar.component';
import { LoginComponent } from './components/login/login.component';
import { CreateAccountComponent } from './components/create-account/create-account.component';
import { AuthPageComponent } from './components/auth-page/auth-page.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { CreateAdminComponent } from './components/create-admin/create-admin.component';
import { DevicesPageComponent } from './components/devices-page/devices-page.component';
import { DeviceCardComponent } from './components/device-card/device-card.component';
import { AddDevicePageComponent } from './components/add-device-page/add-device-page.component';
import { ResetPassowrdComponent } from './components/reset-passowrd/reset-passowrd.component';
import { JwtInterceptor } from './interceptor/jwt.interceptor';
import { SessionExpiredCheckInterceptor } from './interceptor/session-expired-check.interceptor';
import { AmbientSensorComponent } from './components/pka-devices/ambient-sensor/ambient-sensor-history-component/ambient-sensor-history.component';
import { NgApexchartsModule } from 'ng-apexcharts';
import { GraphComponent } from './components/graph/graph.component';
import {AirConditionHistoryComponent} from './components/pka-devices/air-condition/air-condition-history/air-condition-history.component';
import { AirConditionFilterComponent } from './components/pka-devices/air-condition/air-condition-filter/air-condition-filter.component';
import { ToggleSwitchComponent } from './components/toggle-switch/toggle-switch.component';
import { GateMoreInfoCardComponent } from './components/spu-devices/gate/gate-more-info-card/gate-more-info-card.component';
import { AirConditionerMoreInfoCardComponent } from './components/pka-devices/air-condition/air-conditioner-more-info-card/air-conditioner-more-info-card.component'
import { LampInfoPageComponent } from './components/spu-devices/lamp/lamp-info-page/lamp-info-page.component';
import { AirConditionerManagerComponent } from './components/pka-devices/air-condition/air-conditioner-manager/air-conditioner-manager.component';
import { PowerConsumptionComponent } from './components/power-consumption/power-consumption.component';
import { SolarPanelSystemDetailsComponent } from './components/solar-panel-system-details/solar-panel-system-details.component';
import { GateInfoPageComponent } from './components/spu-devices/gate/gate-info-page/gate-info-page.component';
import { LampLightLevelGraphComponent } from './components/spu-devices/lamp/lamp-light-level-graph/lamp-light-level-graph.component';
import { TimeRangeDropdownSelectComponent } from './components/spu-devices/helpers/time-range-dropdown-select/time-range-dropdown-select.component';
import { LampActionsTableComponent } from './components/spu-devices/lamp/lamp-actions-table/lamp-actions-table.component';
import { StartEndDateInputComponent } from './components/spu-devices/helpers/start-end-date-input/start-end-date-input.component';
import { LampBulbOnGraphComponent } from './components/spu-devices/lamp/lamp-bulb-on-graph/lamp-bulb-on-graph.component';
import { GateActionsHistoryTableComponent } from './components/spu-devices/gate/gate-actions-history-table/gate-actions-history-table.component';
import { SprinklerSystemScheduleCardComponent } from './components/spu-devices/sprinkler-system/sprinkler-system-schedule-card/sprinkler-system-schedule-card.component';
import { ScheduleCardComponent } from './components/spu-devices/sprinkler-system/schedule-card/schedule-card.component';
import { SprinklerSystemInfoPageComponent } from './components/spu-devices/sprinkler-system/sprinkler-system-info-page/sprinkler-system-info-page.component';;
import { SprinklerSystemHistoryActionsTableComponent } from './components/spu-devices/sprinkler-system/sprinkler-system-history-actions-table/sprinkler-system-history-actions-table.component';
import { ElectricVehicleChargerDetailsComponent } from './components/electric-vehicle-charger-details/electric-vehicle-charger-details.component';
import { GateInOutHistoryTableComponent } from './components/spu-devices/gate/gate-in-out-history-table/gate-in-out-history-table.component';
import { WashingMachineManagerComponent } from './components/pka-devices/washing-machine/washing-machine-manager/washing-machine-manager.component';
import { WashingMachineHistoryComponent } from './components/pka-devices/washing-machine/washing-machine-history/washing-machine-history.component';
import { WashingMachineMoreInfoCardComponent } from './components/pka-devices/washing-machine/washing-machine-more-info-card/washing-machine-more-info-card.component';
import { WashingMachineMoreInfoFilterComponent } from './components/pka-devices/washing-machine/washing-machine-more-info-filter/washing-machine-more-info-filter.component';
import { PermissionPropertyCardComponent } from './components/permission/permission-property-card/permission-property-card.component';
import { ObtainedPermissionDevicesPageComponent } from './components/permission/obtained-permission-devices-page/obtained-permission-devices-page.component';
import { PermissionManagmentPageComponent } from './components/permission/permission-managment-page/permission-managment-page.component';
import { FilterPermissionsComponent } from './components/permission/filter-permissions/filter-permissions.component';
import { OnlineOfflineGraphComponent } from './components/online-offline/online-offline-graph/online-offline-graph.component';
import { HomeBatteryInfoPageComponent } from './components/home-battery-info-page/home-battery-info-page.component';
import { AdminPowerConsumptionComponent } from './components/admin-power-consumption/admin-power-consumption.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    AuthPageComponent,
    PropertiesPageComponent,
    PropertyCardComponent,
    AddPropertyPageComponent,
    LoginComponent,
    CreateAccountComponent,
    CreateAdminComponent,
    DevicesPageComponent,
    DeviceCardComponent,
    AddDevicePageComponent,
    ResetPassowrdComponent,
    AmbientSensorComponent,
    GraphComponent,
    AirConditionHistoryComponent,
    AirConditionFilterComponent,
    ToggleSwitchComponent,
    GateMoreInfoCardComponent,
    AirConditionerMoreInfoCardComponent,
    LampInfoPageComponent,
    AirConditionerManagerComponent,
    PowerConsumptionComponent,
    SolarPanelSystemDetailsComponent,
    GateInfoPageComponent,
    LampLightLevelGraphComponent,
    TimeRangeDropdownSelectComponent,
    LampActionsTableComponent,
    StartEndDateInputComponent,
    LampBulbOnGraphComponent,
    GateActionsHistoryTableComponent,
    SprinklerSystemScheduleCardComponent,
    ScheduleCardComponent,
    SprinklerSystemInfoPageComponent,
    SprinklerSystemHistoryActionsTableComponent,
    ElectricVehicleChargerDetailsComponent,
    GateInOutHistoryTableComponent,
    WashingMachineManagerComponent,
    WashingMachineHistoryComponent,
    WashingMachineMoreInfoCardComponent,
    WashingMachineMoreInfoFilterComponent,
    PermissionPropertyCardComponent,
    ObtainedPermissionDevicesPageComponent,
    PermissionManagmentPageComponent,
    FilterPermissionsComponent,
    OnlineOfflineGraphComponent,
    HomeBatteryInfoPageComponent,
    AdminPowerConsumptionComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    LeafletModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    NgApexchartsModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: SessionExpiredCheckInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
