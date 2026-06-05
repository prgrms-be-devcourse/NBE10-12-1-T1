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
