export interface PermissionFilter {
    isFilterByEmail:boolean;
    isFilterByPropertyName:boolean;
    isFilterByDeviceName:boolean;
    
    email:string;
    propertyName:string;
    deviceName:string;
}