class LockAccessesController < ApplicationController
  before_action :set_lock
  before_action :set_lock_access, only: %i[show update destroy]
  before_action :use_auth

  # GET /lock/:lock_id/lock_accesses
  def index
    authorize @lock, :show?
    @lock_accesses = @lock.lock_accesses.all

    render json: @lock_accesses
  end

  # GET /lock/:lock_id/lock_accesses/1
  def show
    authorize @lock_access
    render json: @lock_access
  end

  # POST /lock/:lock_id/lock_accesses
  def create
    # create user if not exist
    user = User.first_or_create_by_email(params[:lock_access][:user_email])
    permission = params[:lock_access][:permission]
    expired_at = params[:lock_access][:expired_at]
    @lock_access = LockAccess.where(lock_id: @lock.id, user_id: user.id).first_or_initialize
    @lock_access.permission = permission
    @lock_access.expired_at = expired_at

    authorize @lock_access

    if @lock_access.save
      render json: @lock_access
    else
      render json: @lock_access.errors, status: :unprocessable_entity
    end
  end

  # PATCH/PUT /lock/:lock_id/lock_accesses/1
  def update
    create
  end

  # DELETE /lock/:lock_id/lock_accesses/1
  def destroy
    authorize @lock_access
    @lock_access.destroy
  end

  private

  # Use callbacks to share common setup or constraints between actions.
  def set_lock_access
    @lock_access = @lock.lock_accesses.find(params[:id])
  end

  # Only allow a list of trusted parameters through.
  def lock_access_params
    params.require(:lock_access).permit(:user_email, :permission, :expired_at)
  end

  def set_lock
    @lock = Lock.find(params[:lock_id])
  end
end
