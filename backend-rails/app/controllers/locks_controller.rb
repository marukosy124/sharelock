class LocksController < ApplicationController
  before_action :set_lock, only: %i[show update destroy]
  before_action :use_auth

  # GET /locks
  def index
    @locks = current_user.locks.all

    render json: @locks
  end

  # GET /locks/1
  def show
    authorize @lock

    user = current_user
    @lock_history = @lock.lock_histories.create(:user_id => user.id, :accessed_at => Time.now).save!
    render json: @lock
  end

  # POST /locks
  def create
    @lock = Lock.new(lock_params)

    if @lock.save
      # upon create, set self to admin
      user = current_user
      @lock.lock_accesses.create(:user_id => user.id, :permission => :owner).save!
      render json: @lock, status: :created, location: @lock
    else
      render json: @lock.errors, status: :unprocessable_entity
    end
  end

  # PATCH/PUT /locks/1
  def update
    authorize @lock
    if @lock.update(lock_params)
      render json: @lock
    else
      render json: @lock.errors, status: :unprocessable_entity
    end
  end

  # DELETE /locks/1
  def destroy
    authorize @lock
    @lock.destroy
  end

  private

  # Use callbacks to share common setup or constraints between actions.
  def set_lock
    @lock = Lock.find(params[:id])

    @lock.token = get_token(@lock.private_key)
  end

  # Only allow a list of trusted parameters through.
  def lock_params
    params.require(:lock).permit(:name, :private_key)
  end

  def get_token(key)
    valid_for = 60 * 5
    exp = Time.now.to_i + valid_for
    payload = { data: 'data', exp: exp }

    JWT.encode payload, key, 'HS256'
  end
end
