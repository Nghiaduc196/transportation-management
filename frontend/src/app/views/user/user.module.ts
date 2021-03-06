import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {UserListComponent} from './user-list/user-list.component';
import {UserRoutingModule} from './user-routing.module';
import { UserUpdateComponent } from './user-update/user-update.component';
import {PaginatorModule} from 'primeng/paginator';
import {ToggleButtonModule} from 'primeng/togglebutton';
import {SplitButtonModule} from 'primeng/splitbutton';
import {InputTextModule} from 'primeng/inputtext';
import {ButtonModule} from 'primeng/button';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {InputTextareaModule} from 'primeng/inputtextarea';
import {NgbDropdownModule} from '@ng-bootstrap/ng-bootstrap';
import {RippleModule} from 'primeng/ripple';
import {TableModule} from 'primeng/table';
import {NgSelectModule} from '@ng-select/ng-select';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {DialogModule} from 'primeng/dialog';
@NgModule({
  declarations: [UserListComponent, UserUpdateComponent],
  imports: [
    CommonModule,
    UserRoutingModule,
    PaginatorModule,
    ToggleButtonModule,
    SplitButtonModule,
    InputTextModule,
    ButtonModule,
    FormsModule,
    InputTextareaModule,
    ReactiveFormsModule,
    NgbDropdownModule,
    RippleModule,
    TableModule,
    NgSelectModule,
    ConfirmDialogModule,
    DialogModule
  ]
})
export class UserModule { }
