Rails.application.routes.draw do
  resources :locks do
    resources :lock_accesses
    resources :lock_histories, only: [:index, :show, :destroy], path: 'histories'
  end
  resources :users
  # Define your application routes per the DSL in https://guides.rubyonrails.org/routing.html

  # Defines the root path route ("/")
  root 'welcome#index'
end
