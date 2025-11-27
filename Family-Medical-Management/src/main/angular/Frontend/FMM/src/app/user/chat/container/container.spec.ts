import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ChatContainer } from './container';

describe('ChatContainer', () => {
  let component: ChatContainer;
  let fixture: ComponentFixture<ChatContainer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChatContainer]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChatContainer);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

