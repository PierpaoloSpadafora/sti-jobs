import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-page-card',
  templateUrl: './page-card.component.html',
  styleUrls: ['./page-card.component.css']
})
export class PageCardComponent {
  @Input() title: string = '';
  @Input() buttonText: string = '';
  @Input() icon: string = '';
  @Input() iconSecond: string = '';
  @Input() link: string = '';
}
