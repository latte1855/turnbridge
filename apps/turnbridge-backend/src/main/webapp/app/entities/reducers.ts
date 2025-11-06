import uploadJob from 'app/entities/upload-job/upload-job.reducer';
import uploadJobItem from 'app/entities/upload-job-item/upload-job-item.reducer';
import storedObject from 'app/entities/stored-object/stored-object.reducer';
import trackRange from 'app/entities/track-range/track-range.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  uploadJob,
  uploadJobItem,
  storedObject,
  trackRange,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
