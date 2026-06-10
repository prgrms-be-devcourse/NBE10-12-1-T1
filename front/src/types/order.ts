export interface AdminOrder {
  id: number;
  deliveryId: number;
  email: string;
  address: string;
  totalPrice: number;
  status: string;
  createdAt: string;
}

export interface Product {
  id: number;
  name: string;
  price: number;
  stock?: number;
  imgUrl: string;
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
