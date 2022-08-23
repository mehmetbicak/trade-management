import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { IQueryParams, createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IExceptionMessage, defaultValue } from 'app/shared/model/exception-message.model';

const initialState: EntityState<IExceptionMessage> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

const apiUrl = 'api/exception-messages';

// Actions

export const getEntities = createAsyncThunk('exceptionMessage/fetch_entity_list', async ({ page, size, sort }: IQueryParams) => {
  const requestUrl = 'https://10444706-8d64-4055-a3d2-26d2bc9933e1.mock.pstmn.io/api/exception-messages'; // `${apiUrl}?cacheBuster=${new Date().getTime()}`;
  // eslint-disable-next-line no-console
  console.log('getEntities called for Exception-Message');
  // eslint-disable-next-line no-console
  console.log(requestUrl);
  return axios.get<IExceptionMessage[]>(requestUrl);
});

export const retryEntity = createAsyncThunk(
  'exceptionMessage/retry_entity',
  async (entity: IExceptionMessage, thunkAPI) => {
    const requestUrl = 'https://10444706-8d64-4055-a3d2-26d2bc9933e1.mock.pstmn.io/api/exception-messages/retry/1'; // `${apiUrl}?cacheBuster=${new Date().getTime()}`;
  // eslint-disable-next-line no-console
  console.log('requestUrl:' + requestUrl);
    const result = await axios.put<IExceptionMessage>(requestUrl, cleanEntity(entity));
  // eslint-disable-next-line no-console
  console.log('RETRY SUCCESS:' + JSON.stringify(result));
//    const result = await axios.put<IExceptionMessage>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));

    return result;
  },
  { serializeError: serializeAxiosError }
);

// slice

export const ExceptionMessageSlice = createEntitySlice({
  name: 'exceptionMessage',
  initialState,
  extraReducers(builder) {
    builder
      .addMatcher(isFulfilled(getEntities), (state, action) => {
        const { data } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data,
        };
      })
      .addMatcher(isFulfilled(retryEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isPending(getEntities), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(retryEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = ExceptionMessageSlice.actions;

// Reducer
export default ExceptionMessageSlice.reducer;
