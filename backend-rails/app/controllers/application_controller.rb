class ApplicationController < ActionController::API
  include AuthHelper

  include Pundit::Authorization
  rescue_from Pundit::NotAuthorizedError, with: :user_not_authorized

  private

  def user_not_authorized
    data = { "message" => "Insufficient Permission" }
    render json: data, status: :unauthorized
  end
end
