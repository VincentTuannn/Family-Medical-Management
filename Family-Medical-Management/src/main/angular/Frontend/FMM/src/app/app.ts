import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from './user/homepage/header/header';
import { Menu } from './user/homepage/menu/menu';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Menu],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected title = 'FMM';
}
