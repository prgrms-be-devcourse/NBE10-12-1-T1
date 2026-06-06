export interface Product {
  id: number;
  name: string;
  origin: string;
  price: number;
  imageUrl: string;
}

export interface CartItem {
  product: Product;
  quantity: number;
}

export type OrderStatus = 'pending' | 'delivered';

export interface Order {
  id: string;
  createdAt: Date;
  items: CartItem[];
  total: number;
  email: string;
  address: string;
  zipcode: string;
  status: OrderStatus;
}
