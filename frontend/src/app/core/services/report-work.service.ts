import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import * as _ from 'lodash';

@Injectable({
  providedIn: 'root'
})

export class ReportWorkService {
  private SERVER_API_URL = environment.api;

  constructor(private http: HttpClient) {}

  getAll(param?): Observable<HttpResponse<any>> {
    const data = _.omitBy(param, _.isNil);
    return this.http.get<any>(this.SERVER_API_URL + 'report-work', {params: data, observe: 'response'});
  }

  create(body): Observable<any> {
    return this.http.post<any>(this.SERVER_API_URL + 'report-work', body, { observe: 'response' });
  }

  getReportById(id?): Observable<HttpResponse<any>> {
    return this.http.get<any>(this.SERVER_API_URL + 'report-work/' + id, {
      observe: 'response',
    });
  }

  // update(body): Observable<any> {
  //   return this.http.put<any>(this.SERVER_API_URL + 'position', body, { observe: 'response' });
  // }
  //
  // changeStatus(body): Observable<any> {
  //   return this.http.put<any>(this.SERVER_API_URL + 'position/update-status', body, { observe: 'response' });
  // }
  //
  // delete(id: any): Observable<any> {
  //   return this.http.delete(`${this.SERVER_API_URL + 'position/'}${id}`, { observe: 'response' });
  // }
}
