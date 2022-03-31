class LockHistoriesController < ApplicationController
  before_action :set_lock
  before_action :set_lock_history, only: %i[show update destroy]
  before_action :use_auth

  # GET /lock/:lock_id/histories
  def index
    authorize @lock, :update?
    @lock_histories = @lock.lock_histories.all

    render json: @lock_histories
  end

  # GET /lock/:lock_id/histories/1
  def show
    authorize @lock, :destroy?
    render json: @lock_history
  end

  # does not have history create or update function
  # lock histories are created when when user call GET /locks/1
  #

  # DELETE /lock/:lock_id/histories/i
  def destroy
    authorize @lock, :destroy?
    @lock_history.destroy
  end


  private

  def set_lock
    @lock = Lock.find(params[:lock_id])
  end

  def set_lock_history
    @lock_history = @lock.lock_histories.find(params[:id])
  end

  # Only allow a list of trusted parameters through.
  def lock_history_params
    params.require(:lock_history).permit(:user_email)
  end
end
